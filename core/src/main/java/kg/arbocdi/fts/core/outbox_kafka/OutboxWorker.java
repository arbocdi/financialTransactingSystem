package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.outbox.OutboxEvent;
import kg.arbocdi.fts.core.outbox.OutboxEventRepository;
import kg.arbocdi.fts.core.outbox.TraceContextRestorer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Component
@RequiredArgsConstructor
public class OutboxWorker {
    private final OutboxEventRepository repository;
    private final ObjectProvider<TraceContextRestorer> traceContextRestorerObjectProvider;
    private final ObjectMapper objectMapper;

    @Async("outboxExecutor")
    public Future<Void> runOnce(List<OutboxEvent> shard, KafkaTemplate<String, String> kafkaTemplate) {
        try {
            kafkaTemplate.executeInTransaction(kt -> {
                for (OutboxEvent e : shard) {
                    TraceContextRestorer restorer = traceContextRestorerObjectProvider.getIfAvailable();
                    if (restorer != null)
                        restorer.withRestoredTrace(
                                e.getHeadersMap(objectMapper),
                                "outbox-event:" + e.getPayloadAsMessage(objectMapper).getClass().getSimpleName(),
                                () -> kt.send(e.getTopic(), e.getKey(), e.getPayload())
                        );
                    else kt.send(e.getTopic(), e.getKey(), e.getPayload());
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
