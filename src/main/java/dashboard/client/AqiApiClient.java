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
public class AqiApiClient {
    private static final Logger logger = LoggerFactory.getLogger(AqiApiClient.class);
    private final RestClient restClient;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

    public JsonNode fetchAqi(Double lat, Double lon) {
        String url = String.format("https://air-quality.p.rapidapi.com/current/airquality?lon=%s&lat=%s", lon, lat);
        logger.debug("Fetching aqi, lat: {}, lon: {}", lat, lon);

        JsonNode response = restClient.get()
                .uri(url)
                .header("x-rapidapi-key", rapidApiKey)
                .header("x-rapidapi-host", "air-quality.p.rapidapi.com")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        logger.debug("Fetching aqi - success - {}", response);
        return response;
    }
}
