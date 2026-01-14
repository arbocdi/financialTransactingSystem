package kg.arbocdi.fts.core.rest;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient restClient(RestClient.Builder builder, JsonMapper mapper) {

        // 1) ПУЛ (connection manager)
        PoolingHttpClientConnectionManager cm =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setMaxConnTotal(200)      // общий лимит
                        .setMaxConnPerRoute(50)    // на один host:port
                        .build();

        // 2) Таймауты (очень важно!)
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Timeout.ofSeconds(20))                 // TCP connect
                .setConnectionRequestTimeout(Timeout.ofSeconds(20))       // ждать свободное соединение из пула
                .setResponseTimeout(Timeout.ofSeconds(30))               // ждать ответ (read timeout)
                .build();

        // 3) Клиент
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(cm)
                .setDefaultRequestConfig(requestConfig)
                .evictExpiredConnections()                               // вычищать протухшие
                .evictIdleConnections(Timeout.ofSeconds(30))             // вычищать простаивающие
                .build();

        ClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);

        RestErrorHandler errorHandler = new RestErrorHandler(mapper);

        return builder
                .requestFactory(factory)
                .defaultStatusHandler(errorHandler, errorHandler)
                .build();
    }

}
