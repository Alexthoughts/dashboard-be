package dashboard.service;

import dashboard.client.CurrenciesApiClient;
import dashboard.dto.ConvertResponseDto;
import dashboard.dto.fe.ConvertRateFeDto;
import dashboard.dto.fe.ResponseFeDTO;
import dashboard.dto.fe.SupportedCurrenciesFeDto;
import dashboard.dto.fe.UpdateSavedRatesFeDto;
import dashboard.entity.ConvertRateEntity;
import dashboard.entity.SupportedCurrenciesEntity;
import dashboard.mapper.ConvertRateMapper;
import dashboard.repository.ConvertRateRepository;
import dashboard.repository.SupportedCurrenciesRepository;
import dashboard.util.HelperMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrenciesService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);
    private final ConvertRateMapper convertRateMapper;
    private final SupportedCurrenciesRepository supportedCurrenciesRepository;
    private final ConvertRateRepository convertRateRepository;
    private final CurrenciesApiClient currenciesApiClient;
    private final HelperMethods helperMethods;

    @Transactional
    public ResponseFeDTO<List<SupportedCurrenciesFeDto>> getCurrenciesList() {
        ResponseFeDTO<List<SupportedCurrenciesFeDto>> supportedCurrenciesResponse = new ResponseFeDTO<>();
        List<String> supportedCurrenciesResponseErrorsList = new ArrayList<>();

        List<SupportedCurrenciesFeDto> supportedCurrencies = getSupportedCurrencies(supportedCurrenciesResponseErrorsList);
        List<SupportedCurrenciesEntity> savedEntityList;
        supportedCurrenciesResponse.setErrors(supportedCurrenciesResponseErrorsList);

        if (!supportedCurrencies.isEmpty()) {
            List<SupportedCurrenciesEntity> supportedCurrenciesList = convertRateMapper.fromSupportedCurrenciesFeDtoListToSupportedCurrenciesEntityList(supportedCurrencies);
            supportedCurrenciesRepository.deleteAll();
            savedEntityList = supportedCurrenciesRepository.saveAll(supportedCurrenciesList);

            supportedCurrenciesResponse.setData(convertRateMapper.fromSupportedCurrenciesEntityListToSupportedCurrenciesFeDtoList(savedEntityList));
            return supportedCurrenciesResponse;
        }

        return supportedCurrenciesResponse;
    }

    public ResponseFeDTO<ConvertRateFeDto> getTheRate(String from, String to) {
        ResponseFeDTO<ConvertRateFeDto> ratesResponse = new ResponseFeDTO<>();
        List<String> ratesResponseErrorsList = new ArrayList<>();

        ConvertResponseDto response = getConvertedRate(from, to, ratesResponseErrorsList);
        ratesResponse.setErrors(ratesResponseErrorsList);

        if (response.success() == null || !response.success()) {
            ratesResponseErrorsList.add(response.validationMessage().toString());
            ratesResponse.setErrors(ratesResponseErrorsList);
            return ratesResponse;
        }

        SupportedCurrenciesEntity fromCurrency = supportedCurrenciesRepository.findBySymbol(response.result().from());
        SupportedCurrenciesEntity toCurrency = supportedCurrenciesRepository.findBySymbol(response.result().to());

        ConvertRateEntity convertRateEntity = new ConvertRateEntity();
        convertRateEntity.setFromCurrencyId(fromCurrency);
        convertRateEntity.setToCurrencyId(toCurrency);
        String trimmedRate = helperMethods.roundTwoDecimalsAndConvertToString(response.result().convertedAmount());
        convertRateEntity.setConvertedAmount(trimmedRate);

        ConvertRateEntity savedEntity = convertRateRepository.save(convertRateEntity);

        ratesResponse.setData(convertRateMapper.fromConverRateEntityToConvertRateFeDto(savedEntity));

        return ratesResponse;
    }

    public ResponseFeDTO<UpdateSavedRatesFeDto> getSavedRates() {
        ResponseFeDTO<UpdateSavedRatesFeDto> updatedRatesResponse = new ResponseFeDTO<>();
        List<String> ratesResponseErrorsList = new ArrayList<>();

        List<ConvertRateEntity> savedRatesEntitiesList = convertRateRepository.findAll();

        if (savedRatesEntitiesList.isEmpty()) {
            updatedRatesResponse.setData(new UpdateSavedRatesFeDto(Collections.emptyList(), null));
            return updatedRatesResponse;
        }

        try {
            List<ConvertRateEntity> updatedRatesEntitiesList = savedRatesEntitiesList.stream().peek(savedEntity -> {
                        String fromSymbol = savedEntity.getFromCurrencyId().getSymbol();
                        String toSymbol = savedEntity.getToCurrencyId().getSymbol();
                        ConvertResponseDto response = getConvertedRate(fromSymbol, toSymbol, ratesResponseErrorsList);

                        if (response.success()) {
                            String updatedRate = helperMethods.roundTwoDecimalsAndConvertToString(response.result().convertedAmount());
                            savedEntity.setConvertedAmount(updatedRate);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.validationMessage().toString());
                        }

                    })
                    .toList();

            List<ConvertRateEntity> updatedEntities = convertRateRepository.saveAll(updatedRatesEntitiesList);

            List<ConvertRateFeDto> ratesForFe = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(updatedEntities);
            updatedRatesResponse.setErrors(ratesResponseErrorsList);
            updatedRatesResponse.setData(new UpdateSavedRatesFeDto(ratesForFe, Optional.of(true)));
            return updatedRatesResponse;

        } catch (RestClientException e) {
            List<ConvertRateFeDto> fallbackRates = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(savedRatesEntitiesList);
            updatedRatesResponse.setErrors(ratesResponseErrorsList);
            updatedRatesResponse.setData(new UpdateSavedRatesFeDto(fallbackRates, Optional.of(false)));
            return updatedRatesResponse;
        }
    }

    public void deleteSavedRate(Long id) {
        ConvertRateEntity entity = convertRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("rate with id %d doesn't exist", id))
                );

        convertRateRepository.delete(entity);
    }

    private List<SupportedCurrenciesFeDto> getSupportedCurrencies(List<String> supportedCurrenciesResponseErrorsList) {
        try {
            return currenciesApiClient.fetchSupportedCurrencies();

        } catch (RestClientResponseException ex) {
            supportedCurrenciesResponseErrorsList.add("Supported currencies API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            supportedCurrenciesResponseErrorsList.add("Supported currencies API call failed: " + ex.getMessage() + ", it could be a bad request");
        }
        return Collections.emptyList();
    }

    private ConvertResponseDto getConvertedRate(String from, String to, List<String> ratesResponseErrorsList) {
        try {
            return currenciesApiClient.fetchConvertedRate(from, to);

        } catch (RestClientResponseException ex) {
            ratesResponseErrorsList.add("API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            logger.error("API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
            return new ConvertResponseDto(null, false, List.of(ex.getResponseBodyAsString()));
        } catch (RestClientException ex) {
            ratesResponseErrorsList.add("Currency conversion service call failed: " + ex.getMessage());
            logger.error("API error: {}", ex.getMessage(), ex);
            return new ConvertResponseDto(null, false, List.of(ex.getMessage()));
        }
    }
}
