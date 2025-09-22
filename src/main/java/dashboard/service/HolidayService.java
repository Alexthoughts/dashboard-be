package dashboard.service;

import dashboard.client.HolidayApiClient;
import dashboard.config.CacheConfig;
import dashboard.dto.HolidayResponseDto;
import dashboard.dto.fe.HolidayFeDto;
import dashboard.dto.fe.ResponseFeDTO;
import dashboard.entity.HolidayEntity;
import dashboard.mapper.HolidayMapper;
import dashboard.repository.HolidayRepository;
import dashboard.service.mocks.HolidayServiceMock;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private static final Logger logger = LoggerFactory.getLogger(HolidayService.class);

    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;
    private final HolidayServiceMock holidayServiceMock;
    private final HolidayApiClient holidayApiClient;
    private final CacheConfig cacheConfig;

    @Value("${holiday-service-mock}")
    boolean isHolidayServiceMock;

    public ResponseFeDTO<List<HolidayFeDto>> getHolidayList(String countryCode) {
        ResponseFeDTO<List<HolidayFeDto>> cached = getFromCache(countryCode);
        if (cached != null) {
            logger.info("Returning holidays from cache for countryCode={}, {}", countryCode, cached);
            return cached;
        }

        return getHolidayListFromDbAndApi(countryCode);
    }

    @Transactional
    private ResponseFeDTO<List<HolidayFeDto>> getHolidayListFromDbAndApi(String countryCode) {
        List<String> errors = new ArrayList<>();
        List<HolidayResponseDto> holidayList = fetchAndUpdate(countryCode, errors);

        if (holidayList == null || holidayList.isEmpty()) {
            ResponseFeDTO<List<HolidayFeDto>> response = new ResponseFeDTO<>();
            response.setErrors(errors);
            response.setData(Collections.emptyList());
            return response;
        }

        ResponseFeDTO<List<HolidayFeDto>> response = saveAndBuildResponse(holidayList, errors);
        putToCache(countryCode, response);
        return response;
    }

    @SuppressWarnings("unchecked")
    private ResponseFeDTO<List<HolidayFeDto>> getFromCache(String countryCode) {
        var holidaysCache = (CaffeineCache) cacheConfig.cacheManager().getCache("holidaysCache");
        if (holidaysCache == null) {
            logger.error("Cache 'holidaysCache' is not configured!");
            return null;
        }
        return holidaysCache.get(countryCode, ResponseFeDTO.class);
    }

    private List<HolidayResponseDto> fetchAndUpdate(String countryCode, List<String> errors) {
        if (isHolidayServiceMock) {
            return holidayServiceMock.getMockHolidayList();
        }
        return getHolidaysRest(Year.now(), countryCode, errors);
    }

    private ResponseFeDTO<List<HolidayFeDto>> saveAndBuildResponse(List<HolidayResponseDto> holidayList,
                                                                   List<String> errors) {
        List<HolidayEntity> entities =
                holidayMapper.holidayResponseDtoListToHolidayEntityList(holidayList);

        holidayRepository.deleteAllInBatch();
        holidayRepository.saveAll(entities);

        List<HolidayFeDto> dtoList = holidayMapper.holidayEntityListToHolidayFeDtoList(entities);

        ResponseFeDTO<List<HolidayFeDto>> response = new ResponseFeDTO<>();
        response.setData(dtoList);
        response.setErrors(errors);
        return response;
    }

    private void putToCache(String countryCode, ResponseFeDTO<List<HolidayFeDto>> response) {
        var holidaysCache = (CaffeineCache) cacheConfig.cacheManager().getCache("holidaysCache");
        if (holidaysCache != null) {
            holidaysCache.put(countryCode, response);
        }
    }

    private List<HolidayResponseDto> getHolidaysRest(Year currentYear,
                                                     String countryCode,
                                                     List<String> errors) {
        try {
            return holidayApiClient.fetchHolidays(currentYear, countryCode);
        } catch (RestClientResponseException ex) {
            errors.add("Holiday API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            logger.error("Holiday API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            errors.add("Holiday API call failed: " + ex.getMessage());
            logger.error("Holiday API call failed: {}", ex.getMessage(), ex);
        }
        return Collections.emptyList();
    }
}
