package dashboard.service;

import dashboard.client.HolidayApiClient;
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

    @Value("${holiday-service-mock}")
    boolean isHolidayServiceMock;

    @Transactional
    public ResponseFeDTO<List<HolidayFeDto>> getHolidayList(String countryCode) {
        ResponseFeDTO<List<HolidayFeDto>> holidaysResponse = new ResponseFeDTO<>();
        List<String> holidaysResponseErrorsList = new ArrayList<>();
        List<HolidayResponseDto> holidayList;

        if (isHolidayServiceMock) {
            holidayList = holidayServiceMock.getMockHolidayList();
        } else {
            holidayList = getHolidaysRest(Year.now(), countryCode, holidaysResponseErrorsList);
        }

        if (holidayList != null && !holidayList.isEmpty()) {
            List<HolidayEntity> entities = holidayMapper.holidayResponseDtoListToHolidayEntityList(holidayList);
            holidayRepository.deleteAll();
            holidayRepository.saveAll(entities);
        }

        List<HolidayEntity> holidaysInDbList = holidayRepository.findAll();

        holidaysResponse.setData(holidayMapper.holidayEntityListToHolidayFeDtoList(holidaysInDbList));
        holidaysResponse.setErrors(holidaysResponseErrorsList);
        return holidaysResponse;
    }

    private List<HolidayResponseDto> getHolidaysRest(Year currentYear, String countryCode, List<String> holidaysResponseErrorsList) {
        try {
            return holidayApiClient.fetchHolidays(currentYear, countryCode);

        } catch (RestClientResponseException ex) {
            holidaysResponseErrorsList.add("Holiday API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            logger.error("Holiday API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString(), ex);
        } catch (RestClientException ex) {
            holidaysResponseErrorsList.add("Holiday API call failed: " + ex.getMessage() + ", it could be a bad request");
            logger.error("Holiday API call failed: {}, it could be a bad request", ex.getMessage(), ex);
        }

        return Collections.emptyList();
    }
}
