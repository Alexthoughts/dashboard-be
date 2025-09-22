package dashboard.client;

import dashboard.dto.HolidayResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Year;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HolidayApiClient {
    private static final Logger logger = LoggerFactory.getLogger(HolidayApiClient.class);
    private final RestClient restClient;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

    public List<HolidayResponseDto> fetchHolidays(Year currentYear, String countryCode) {
        String url = String.format("https://public-holidays7.p.rapidapi.com/%s/%s", currentYear, countryCode);
        logger.debug("Fetching holidays for {} - {}", currentYear, countryCode);

        List<HolidayResponseDto> response = restClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", "public-holidays7.p.rapidapi.com")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        logger.debug("Fetching holiday - success - {}", response);
        return response;
    }
}