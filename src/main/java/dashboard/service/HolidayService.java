package dashboard.service;

import dashboard.dto.HolidayResponseDto;
import dashboard.dto.fe.HolidayFeDto;
import dashboard.entity.HolidayEntity;
import dashboard.mapper.HolidayMapper;
import dashboard.repository.HolidayRepository;
import dashboard.service.mocks.HolidayServiceMock;
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

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayService {
    private final RestClient restClient;
    private final HolidayRepository holidayRepository;
    private final HolidayMapper holidayMapper;
    private final HolidayServiceMock holidayServiceMock;

    @Value("${holiday-service-mock}")
    boolean isHolidayServiceMock;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

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

    private List<HolidayResponseDto> getHolidaysRest(Year currentYear, String countryCode) {
        String url = String.format("https://public-holidays7.p.rapidapi.com/%s/%s", currentYear, countryCode);

        try {
            return restClient.get()
                    .uri(url)
                    .header("x-rapidapi-key", rapidApiKey)
                    .header("x-rapidapi-host", "public-holidays7.p.rapidapi.com")
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

        } catch (RestClientResponseException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Holiday API error: " + ex.getStatusCode() + " - " + ex.getResponseBodyAsString());
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Holiday API call failed: " + ex.getMessage() + ", it could be a bad request");
        }
    }
}
