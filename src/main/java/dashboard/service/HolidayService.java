package dashboard.service;

import dashboard.dto.HolidayFeDto;
import dashboard.dto.HolidayResponseDto;
import dashboard.entity.HolidayEntity;
import dashboard.mapper.HolidayMapper;
import dashboard.repository.HolidayRepository;
import dashboard.service.mocks.HolidayServiceMock;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.time.Year;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;
    private final HolidayServiceMock holidayServiceMock;

    @Value("${holiday-service-mock}")
    boolean isHolidayServiceMock;

    @Transactional
    public List<HolidayFeDto> getHolidayList(String countryCode) {
        List<HolidayResponseDto> holidayResponse;

        if (isHolidayServiceMock) {
            holidayResponse = holidayServiceMock.getMockHolidayList();
        } else {
            holidayResponse = getHolidaysRest(Year.now(), countryCode);
        }

        if (holidayResponse != null && !holidayResponse.isEmpty()) {
            List<HolidayEntity> entities = holidayMapper.holidayResponseDtoListToHolidayEntityList(holidayResponse);
            holidayRepository.deleteAll();
            holidayRepository.saveAll(entities);
        }

        List<HolidayEntity> holidaysInDbList = holidayRepository.findAll();

        return holidayMapper.holidayEntityListToHolidayFeDtoList(holidaysInDbList);
    }

    public List<HolidayResponseDto> getHolidaysRest(Year currentYear, String countryCode) {
        String url = String.format("https://public-holidays7.p.rapidapi.com/%s/%s", currentYear, countryCode);

        RestClient restClient = RestClient.create();

        try {
            return restClient.get()
                    .uri(url)
                    .header("x-rapidapi-key", "2545cfc18amsh4fa2481df2d6a5ep13ff72jsn3d0b055227f7")
                    .header("x-rapidapi-host", "public-holidays7.p.rapidapi.com")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (RestClientResponseException ex) {
            System.err.println("Holiday API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
            return Collections.emptyList();
        } catch (RestClientException ex) {
            System.err.println("Holiday API call failed: " + ex.getMessage() + ", it could be a bad request");
            return Collections.emptyList();
        }
    }
}
