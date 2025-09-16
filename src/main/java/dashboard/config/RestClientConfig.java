package dashboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    //    @Bean -> executes method once during setup and store the result in ApplicationContext
    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
