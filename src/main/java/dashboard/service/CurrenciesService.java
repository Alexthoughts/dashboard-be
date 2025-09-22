package dashboard.service;

import dashboard.client.CurrenciesApiClient;
import dashboard.config.CacheConfig;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.caffeine.CaffeineCache;
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
    private final CacheConfig cacheConfig;

    @Transactional
    public ResponseFeDTO<List<SupportedCurrenciesFeDto>> getCurrenciesList() {
        ResponseFeDTO<List<SupportedCurrenciesFeDto>> availableCurrenciesCache = getAvailableCurrenciesCache();
        if (availableCurrenciesCache != null) {
            logger.info("Returning available currencies cache, {}", availableCurrenciesCache);
            return availableCurrenciesCache;
        }

        ResponseFeDTO<List<SupportedCurrenciesFeDto>> supportedCurrenciesResponse = new ResponseFeDTO<>();
        List<String> supportedCurrenciesResponseErrorsList = new ArrayList<>();

        List<SupportedCurrenciesFeDto> supportedCurrencies = fetchSupportedCurrencies(supportedCurrenciesResponseErrorsList);
        supportedCurrenciesResponse.setErrors(supportedCurrenciesResponseErrorsList);

        if (!supportedCurrencies.isEmpty()) {
            List<SupportedCurrenciesEntity> supportedCurrenciesList = convertRateMapper.fromSupportedCurrenciesFeDtoListToSupportedCurrenciesEntityList(supportedCurrencies);
            List<SupportedCurrenciesEntity> savedEntityList = supportedCurrenciesRepository.saveAll(supportedCurrenciesList);
            supportedCurrenciesResponse.setData(convertRateMapper.fromSupportedCurrenciesEntityListToSupportedCurrenciesFeDtoList(savedEntityList));

            putAvailableCurrenciesCache(supportedCurrenciesResponse);
        }

        return supportedCurrenciesResponse;
    }

    @SuppressWarnings("unchecked")
    private ResponseFeDTO<List<SupportedCurrenciesFeDto>> getAvailableCurrenciesCache() {
        var availableCurrenciesCache = (CaffeineCache) cacheConfig.cacheManager().getCache("availableCurrenciesCache");
        if (availableCurrenciesCache == null) return null;
        return availableCurrenciesCache.get("allCurrencies", ResponseFeDTO.class);
    }

    private void putAvailableCurrenciesCache(ResponseFeDTO<List<SupportedCurrenciesFeDto>> response) {
        CaffeineCache availableCurrenciesCache = (CaffeineCache) cacheConfig.cacheManager().getCache("availableCurrenciesCache");
        if (availableCurrenciesCache == null) return;
        availableCurrenciesCache.put("allCurrencies", response);
    }

    private List<SupportedCurrenciesFeDto> fetchSupportedCurrencies(List<String> supportedCurrenciesResponseErrorsList) {
        try {
            return currenciesApiClient.fetchSupportedCurrencies();
        } catch (RestClientResponseException ex) {
            supportedCurrenciesResponseErrorsList.add("Supported currencies API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            supportedCurrenciesResponseErrorsList.add("Supported currencies API call failed: " + ex.getMessage() + ", it could be a bad request");
        }
        return Collections.emptyList();
    }

    @CacheEvict(value = "convertRateCache", allEntries = true)
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
        ConvertRateEntity convertRateEntity;

        String trimmedRate = helperMethods.roundTwoDecimalsAndConvertToString(response.result().convertedAmount());
        Optional<ConvertRateEntity> existing = convertRateRepository.findByFromCurrencyIdAndToCurrencyId(fromCurrency, toCurrency);

        if (existing.isPresent()) {
            // Update existing rate
            convertRateEntity = existing.get();
            convertRateEntity.setConvertedAmount(trimmedRate);
        } else {
            // Insert new
            convertRateEntity = new ConvertRateEntity();
            convertRateEntity.setFromCurrencyId(fromCurrency);
            convertRateEntity.setToCurrencyId(toCurrency);
            convertRateEntity.setConvertedAmount(trimmedRate);
        }

        ConvertRateEntity savedEntity = convertRateRepository.save(convertRateEntity);
        ratesResponse.setData(convertRateMapper.fromConverRateEntityToConvertRateFeDto(savedEntity));

        return ratesResponse;
    }

    public ResponseFeDTO<UpdateSavedRatesFeDto> getSavedRates() {
        ResponseFeDTO<UpdateSavedRatesFeDto> cached = getSavedRatesCache();
        if (cached != null){
            logger.info("Returning saved rates cache, {}", cached);
            return cached;
        }

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
                    ResponseFeDTO<ConvertRateFeDto> rateResponse = new ResponseFeDTO<>();
                    rateResponse.setData(convertRateMapper.fromConverRateEntityToConvertRateFeDto(savedEntity));
                    putConvertRateCache(fromSymbol, toSymbol, rateResponse);
                } else {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, response.validationMessage().toString());
                }
            }).toList();

            List<ConvertRateEntity> updatedEntities = convertRateRepository.saveAll(updatedRatesEntitiesList);
            List<ConvertRateFeDto> ratesForFe = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(updatedEntities);

            updatedRatesResponse.setErrors(ratesResponseErrorsList);
            updatedRatesResponse.setData(new UpdateSavedRatesFeDto(ratesForFe, Optional.of(true)));

            putSavedRatesCache(updatedRatesResponse);

            return updatedRatesResponse;
        } catch (RestClientException e) {
            List<ConvertRateFeDto> fallbackRates = convertRateMapper.fromConvertRateEntityListToConvertRateFeDtoList(savedRatesEntitiesList);

            updatedRatesResponse.setErrors(ratesResponseErrorsList);
            updatedRatesResponse.setData(new UpdateSavedRatesFeDto(fallbackRates, Optional.of(false)));

            return updatedRatesResponse;
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseFeDTO<UpdateSavedRatesFeDto> getSavedRatesCache() {
        var cache = (CaffeineCache) cacheConfig.cacheManager().getCache("convertRateCache");
        if (cache == null) return null;
        return (ResponseFeDTO<UpdateSavedRatesFeDto>) cache.get("allRates", ResponseFeDTO.class);
    }

    private void putSavedRatesCache(ResponseFeDTO<UpdateSavedRatesFeDto> response) {
        var cache = (CaffeineCache) cacheConfig.cacheManager().getCache("convertRateCache");
        if (cache == null) return;
        cache.put("allRates", response);
    }

    @SuppressWarnings("unchecked")
    private ResponseFeDTO<ConvertRateFeDto> getConvertRateCache(String from, String to) {
        var convertRateCache = (CaffeineCache) cacheConfig.cacheManager().getCache("convertRateCache");
        if (convertRateCache == null) return null;
        String key = buildConvertRateCacheKey(from, to);
        return (ResponseFeDTO<ConvertRateFeDto>) convertRateCache.get(key, ResponseFeDTO.class);
    }

    private void putConvertRateCache(String from, String to, ResponseFeDTO<ConvertRateFeDto> response) {
        var convertRateCache = (CaffeineCache) cacheConfig.cacheManager().getCache("convertRateCache");
        if (convertRateCache == null) return;
        String key = buildConvertRateCacheKey(from, to);
        convertRateCache.put(key, response);
    }

    @CacheEvict(value = "convertRateCache", allEntries = true)
    public void deleteSavedRate(Long id) {
        ConvertRateEntity entity = convertRateRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, String.format("rate with id %d doesn't exist", id)));
        convertRateRepository.delete(entity);
    }

    private String buildConvertRateCacheKey(String from, String to) {
        return from + ":" + to;
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