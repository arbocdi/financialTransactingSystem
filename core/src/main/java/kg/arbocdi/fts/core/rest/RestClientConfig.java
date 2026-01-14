package kg.arbocdi.fts.core.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient restClient(RestClient.Builder builder, JsonMapper mapper) {
        RestErrorHandler errorHandler = new RestErrorHandler(mapper);
        return builder
                .defaultStatusHandler(errorHandler, errorHandler)
                .build();
    }
}
