package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.outbox.OutboxEvent;
import kg.arbocdi.fts.core.outbox.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class OutboxWorker {
    private final OutboxEventRepository repository;

    @Async("outboxExecutor")
    public Future<Void> runOnce(List<OutboxEvent> shard, KafkaTemplate<String, String> kafkaTemplate) {
        try {
            kafkaTemplate.executeInTransaction(kt -> {
                for (OutboxEvent e : shard) {
                    kt.send(e.getTopic(), e.getKey(), e.getPayload());
                }
                return true;
            });
            repository.deleteAll(shard);
            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
