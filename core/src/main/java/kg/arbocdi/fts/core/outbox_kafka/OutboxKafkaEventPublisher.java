package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox.OutboxEvent;
import kg.arbocdi.fts.core.outbox.OutboxEventRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxKafkaEventPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository repository;
    private final TopicResolver topicResolver;
    private final KeyExtractor keyExtractor;
    private final ObjectMapper objectMapper;

    private static final int BATCH_SIZE = 50;

    @Scheduled(fixedDelay = 50)
    public void publish() {
        // 1) Берём пачку под отправку (лучше: с локацией/lease, чтобы несколько инстансов не дублировали)
        List<OutboxEvent> events = repository.findForSend(BATCH_SIZE);
        if (events.isEmpty()) return;

        // 2) Отправляем пачку в одной Kafka-транзакции
        kafkaTemplate.executeInTransaction(kt -> {
            for (OutboxEvent e : events) {
                Message msg = e.getPayloadAsMessage(objectMapper);

                String topic = topicResolver.getTopic(msg);
                String key = keyExtractor.getKey(msg);

                kt.send(topic, key, e.getPayload()); // e.getPayload() = String/JSON
            }
            return true;
        });

        // 3) Если дошли сюда — commitTransaction() прошёл успешно
        //    Значит можно пометить события как отправленные.
        repository.markSent(events);
    }

    @Data
    @AllArgsConstructor
    private static class KafkaResult {
        private CompletableFuture<SendResult<String, String>> future;
        private OutboxEvent event;
    }
}
