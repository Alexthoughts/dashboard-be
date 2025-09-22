package dashboard.client;

import dashboard.dto.ConvertResponseDto;
import dashboard.dto.fe.SupportedCurrenciesFeDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrenciesApiClient {
    private static final Logger logger = LoggerFactory.getLogger(CurrenciesApiClient.class);
    private final RestClient restClient;

    @Value("${api.rapidApi.key}")
    String rapidApiKey;

    public List<SupportedCurrenciesFeDto> fetchSupportedCurrencies() {
        logger.debug("Fetching currencies list");

        List<SupportedCurrenciesFeDto> response = restClient.get()
                .uri("https://currency-converter18.p.rapidapi.com/api/v1/supportedCurrencies")
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", "currency-converter18.p.rapidapi.com")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        logger.debug("Fetching currencies list - success - {}", response);
        return response;
    }

    public ConvertResponseDto fetchConvertedRate(String from, String to) {
        String url = String.format(
                "https://currency-converter18.p.rapidapi.com/api/v1/convert?from=%s&to=%s&amount=1",
                from, to
        );
        logger.debug("Fetching convert rate {} - {}", from, to);

        ConvertResponseDto response = restClient.get()
                .uri(url)
                .header("X-RapidAPI-Key", rapidApiKey)
                .header("X-RapidAPI-Host", "currency-converter18.p.rapidapi.com")
                .retrieve()
                .body(ConvertResponseDto.class);

        logger.debug("Fetching convert rate - success - {}", response);
        return response;
    }
}