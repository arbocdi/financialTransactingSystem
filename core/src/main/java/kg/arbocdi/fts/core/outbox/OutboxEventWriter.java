package kg.arbocdi.fts.core.outbox;

import kg.arbocdi.fts.core.EventPublisher;
import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox_kafka.KeyExtractor;
import kg.arbocdi.fts.core.outbox_kafka.SignaledOutboxKafkaEventPublisher;
import kg.arbocdi.fts.core.outbox_kafka.TopicResolver;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@AllArgsConstructor
@Component
public class OutboxEventWriter implements EventPublisher {
    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;
    private final KeyExtractor keyExtractor;
    private final TopicResolver topicResolver;
    private final SignaledOutboxKafkaEventPublisher publisher;
    private final ObjectProvider<TraceContextSnapshotter> traceSnapshotterProvider;


    @Override
    public void publish(Message event) {
        TraceContextSnapshotter snapshotter = traceSnapshotterProvider.getIfAvailable();
        OutboxEvent outboxEvent = new OutboxEvent(event, objectMapper, keyExtractor, topicResolver);
        if (snapshotter != null) {
            Map<String, String> headers = snapshotter.capture();
            outboxEvent.setHeadersMap(headers, objectMapper);
        }
        repository.save(outboxEvent);
        signalAfterCommit();
    }

    private void signalAfterCommit() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    publisher.signalReady();
                }
            });
        }
    }
}
