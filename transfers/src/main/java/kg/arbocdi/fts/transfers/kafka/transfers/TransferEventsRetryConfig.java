package kg.arbocdi.fts.transfers.kafka.transfers;

import kg.arbocdi.fts.core.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;

@Configuration
public class TransferEventsRetryConfig {
        @Value("${spring.kafka.listener.concurrency}")
        private int concurrency;
        @Bean
        public RetryTopicConfiguration transferEventsRetryConf(
                KafkaTemplate<String, String> kafkaTemplate
        ) {
            return RetryTopicConfigurationBuilder
                    .newInstance()
                    // сколько попыток ВСЕГО (main + retries)
                    .maxAttempts(2)
                    // backoff
                    .exponentialBackoff(
                            10_000L,   // initial delay
                            2.0,       // multiplier
                            10 * 60_000L // max delay
                    )
                    // какие сразу в DLT
                    .notRetryOn(BusinessException.class)
                    // suffix’ы
                    .retryTopicSuffix("-retry")
                    .dltSuffix("-dlt")
                    // ⚠️ ВАЖНО: партиции и реплики
                    .autoCreateTopics(true,concurrency,(short)1)
                    // применяем ТОЛЬКО к этому listener’у
                    .includeTopic("transfer-events")
                    .dltHandlerMethod("kafkaTransferEventListener","onDlt")
                    .create(kafkaTemplate);
        }

}
