package dashboard.service;

import dashboard.dto.ConvertResponseDto;
import dashboard.dto.fe.ConvertRateFeDto;
import dashboard.dto.fe.SupportedCurrenciesFeDto;
import dashboard.dto.fe.UpdateSavedRatesFeDto;
import dashboard.entity.ConvertRateEntity;
import dashboard.entity.SupportedCurrenciesEntity;
import dashboard.mapper.ConvertRateMapper;
import dashboard.repository.ConvertRateRepository;
import dashboard.repository.SupportedCurrenciesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CurrenciesService {
    private final RestClient restClient;
    private final ConvertRateMapper convertRateMapper;
    private final SupportedCurrenciesRepository supportedCurrenciesRepository;
    private final ConvertRateRepository convertRateRepository;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

    @Transactional
    public List<SupportedCurrenciesFeDto> getCurrenciesList() {
        List<SupportedCurrenciesFeDto> supportedCurrencies = getSupportedCurrencies();
        List<SupportedCurrenciesEntity> savedEntityList;

        if (!supportedCurrencies.isEmpty()) {
            List<SupportedCurrenciesEntity> supportedCurrenciesList = convertRateMapper.fromSupportedCurrenciesFeDtoListToSupportedCurrenciesEntityList(supportedCurrencies);
            savedEntityList = supportedCurrenciesRepository.saveAll(supportedCurrenciesList);
            return convertRateMapper.fromSupportedCurrenciesEntityListToSupportedCurrenciesFeDtoList(savedEntityList);
        }

        return Collections.emptyList();
    }

    public ConvertRateFeDto getTheRate(String from, String to) {
        ConvertResponseDto response = getConvertedRate(from, to);

        if (!response.success()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.validationMessage().toString());
        }

        SupportedCurrenciesEntity fromCurrency = supportedCurrenciesRepository.findBySymbol(response.result().from());
        SupportedCurrenciesEntity toCurrency = supportedCurrenciesRepository.findBySymbol(response.result().to());

        ConvertRateEntity convertRateEntity = new ConvertRateEntity();
        convertRateEntity.setFromCurrencyId(fromCurrency);
        convertRateEntity.setToCurrencyId(toCurrency);
        Double trimmedRate = roundTwoDecimals(response.result().convertedAmount());
        convertRateEntity.setConvertedAmount(trimmedRate);

        ConvertRateEntity savedEntity = convertRateRepository.save(convertRateEntity);

        return convertRateMapper.fromConverRateEntityToConvertRateFeDto(savedEntity);
    }

    public UpdateSavedRatesFeDto getSavedRates() {
        List<ConvertRateEntity> savedRatesEntitiesList = convertRateRepository.findAll();

        if (savedRatesEntitiesList.isEmpty()) {
            return new UpdateSavedRatesFeDto(Collections.emptyList(), null);
        }

        try {
            List<ConvertRateEntity> updatedRatesEntitiesList = savedRatesEntitiesList.stream().peek(savedEntity -> {
                        String fromSymbol = savedEntity.getFromCurrencyId().getSymbol();
                        String toSymbol = savedEntity.getToCurrencyId().getSymbol();
                        ConvertResponseDto response = getConvertedRate(fromSymbol, toSymbol);

                        if (response.success()) {
                            Double updatedRate = roundTwoDecimals(response.result().convertedAmount());
                            savedEntity.setConvertedAmount(updatedRate);
                        } else {
                            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.validationMessage().toString());
                        }

                    })
                    .toList();

            List<ConvertRateEntity> updatedEntities = convertRateRepository.saveAll(updatedRatesEntitiesList);

            List<ConvertRateFeDto> ratesForFe = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(updatedEntities);

            return new UpdateSavedRatesFeDto(ratesForFe, Optional.of(true));

        } catch (RestClientException e) {
            List<ConvertRateFeDto> fallbackRates = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(savedRatesEntitiesList);
            return new UpdateSavedRatesFeDto(fallbackRates, Optional.of(false));
        }
    }

    public void deleteSavedRate(Long id) {
        ConvertRateEntity entity = convertRateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("rate with id %d doesn't exist", id))
                );

        convertRateRepository.delete(entity);
    }

    private List<SupportedCurrenciesFeDto> getSupportedCurrencies() {
        try {
            return restClient.get()
                    .uri("https://currency-converter18.p.rapidapi.com/api/v1/supportedCurrencies")
                    .header("x-rapidapi-key", rapidApiKey)
                    .header("x-rapidapi-host", "currency-converter18.p.rapidapi.com")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supported currencies API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supported currencies API call failed: " + ex.getMessage() + ", it could be a bad request");
        }
    }

    private ConvertResponseDto getConvertedRate(String from, String to) {
        String url = String.format("https://currency-converter18.p.rapidapi.com/api/v1/convert?from=%s&to=%s&amount=1", from, to);

        try {
            return restClient.get()
                    .uri(url)
                    .header("x-rapidapi-key", rapidApiKey)
                    .header("x-rapidapi-host", "currency-converter18.p.rapidapi.com")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Currency conversion service call failed: " + ex.getMessage(), ex);
        }
    }

    private Double roundTwoDecimals(Double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
