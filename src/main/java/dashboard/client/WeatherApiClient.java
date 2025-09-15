package dashboard.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class WeatherApiClient {
    private static final Logger logger = LoggerFactory.getLogger(WeatherApiClient.class);
    private final RestClient restClient;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

    public JsonNode fetchWeather(Double lat, Double lon, Integer forecastDays) {
        String url = String.format("https://weatherapi-com.p.rapidapi.com/forecast.json?q=%s,%s&days=%d", lat, lon, forecastDays);
        logger.debug("Fetching weather lat: {}, lon: {},  days: {}", lat, lon, forecastDays);

        JsonNode response = restClient.get()
                .uri(url)
                .header("x-rapidapi-key", rapidApiKey)
                .header("x-rapidapi-host", "weatherapi-com.p.rapidapi.com")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        logger.debug("Fetching weather - success - {}", response);
        return response;
    }
}