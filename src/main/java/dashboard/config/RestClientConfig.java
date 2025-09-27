package dashboard.config;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

//    Every 3 minutes, HttpClient removes idle connections.
//    That way, your first request after a pause opens a fresh connection instead of reusing a dead one.
//    It solves the “first call after inactivity fails” problem.

    //    @Bean -> executes method once during setup and store the result in ApplicationContext
    @Bean
    public RestClient restClient() {
        CloseableHttpClient httpClient = HttpClients.custom()
                // Evict idle connections after 3 minutes (RapidAPI often kills at ~60s)
                .evictIdleConnections(TimeValue.ofMinutes(3)) //or 30sec
                .build();

        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        requestFactory.setConnectTimeout(10000); // 5s - how long your client waits to establish a TCP connection to RapidAPI
        requestFactory.setReadTimeout(15000);   // 10s - how long your client waits for a server response after the connection has been established.

        return RestClient.builder()
                .requestFactory(requestFactory)
                .build();
    }
}