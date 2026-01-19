package kg.arbocdi.fts.accountViews.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ContainerCustomizer;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
public class KafkaRetryConfig {

    @Bean
    public ContainerCustomizer<String, String, ConcurrentMessageListenerContainer<String, String>> containerCustomizer(DefaultErrorHandler errorHandler) {

        return container -> {
            // error handler
            container.setCommonErrorHandler(errorHandler());
        };
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        // пример: 1 попытка + 4 ретрая = 5 доставок
        var backoff = new ExponentialBackOffWithMaxRetries(10);
        backoff.setInitialInterval(500);
        backoff.setMultiplier(2.0);
        backoff.setMaxInterval(60_000);
        //ожидание реализовано через thread.sleep
        //во время ожидания heartbeats продолжают идти отд потоком
        //перед каждой задержкой делается poll чтобы избежать ребаланса
        //если ожидание превышает max.poll.interval то нужно сконфигурировать ContainerPausingBackOffHandler

        // recoverer который "не восстанавливает", а принудительно валит обработку
        ConsumerRecordRecoverer rethrowRecoverer =
                (rec, ex) -> {
                    throw new RuntimeException(ex);
                };

        var eh = new DefaultErrorHandler(rethrowRecoverer, backoff);


        // опционально: какие исключения ретраим/не ретраим
        eh.addRetryableExceptions(Exception.class);
        // eh.addNotRetryableExceptions(IllegalArgumentException.class);

        return eh;
    }

}

