package kg.arbocdi.fts.transfers.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {
    @Value("${partitions.number}")
    private int partitions;

    @Bean
    public NewTopic accountEventsTopic() {
        return TopicBuilder.name("account-events")
                .partitions(partitions)
                .replicas(1)   // ⚠️ если один брокер; если кластер — ставь 2/3
                .build();
    }

    @Bean
    public NewTopic transferEventsTopic() {
        return TopicBuilder.name("transfer-events")
                .partitions(partitions)
                .replicas(1)   // ⚠️ если один брокер; если кластер — ставь 2/3
                .build();
    }
}
