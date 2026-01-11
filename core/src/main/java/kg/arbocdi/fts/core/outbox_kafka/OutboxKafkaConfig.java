package kg.arbocdi.fts.core.outbox_kafka;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class OutboxKafkaConfig {
    @Value("${partitions.number}")
    private int partitionsNumber;
    @Value("${kafka.tx.id.prefix}")
    private String txIdPrefix;

    @Bean
    public KafkaTemplates outboxTemplates(
            ProducerFactory<String, String> pf
    ) {
        List<KafkaTemplate<String, String>> list = new ArrayList<>(partitionsNumber);
        //do not create KafkaTransactionManager
        for (int i = 0; i < partitionsNumber; i++) {
            Map<String, Object> ovverrides = new HashMap<>();
            ovverrides.put("transactional.id", txIdPrefix + "-" + i + "-");
            KafkaTemplate<String, String> kt = new KafkaTemplate<>(pf, ovverrides);
            list.add(kt);
        }
        return new KafkaTemplates(list);
    }

    @Data
    public static class KafkaTemplates {
        private final List<KafkaTemplate<String, String>> kafkaTemplates;

        public KafkaTemplate<String, String> get(int index) {
            return kafkaTemplates.get(index);
        }
    }
}