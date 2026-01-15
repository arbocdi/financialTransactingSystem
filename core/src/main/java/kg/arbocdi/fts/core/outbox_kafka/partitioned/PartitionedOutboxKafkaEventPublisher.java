package kg.arbocdi.fts.core.outbox_kafka.partitioned;

import kg.arbocdi.fts.core.outbox.OutboxEvent;
import kg.arbocdi.fts.core.outbox.OutboxEventRepository;
import kg.arbocdi.fts.core.outbox_kafka.OutboxKafkaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;


//@Component
@Slf4j
@RequiredArgsConstructor
public class PartitionedOutboxKafkaEventPublisher {

    private final OutboxKafkaConfig.KafkaTemplates kafkaTemplates;
    private final OutboxEventRepository repository;
    private final OutboxWorker outboxWorker;
    @Value("${partitions.number}")
    private int partitionsNumber;
    private static final int BATCH_SIZE = 200;


    @Scheduled(fixedDelay = 200)
    public void publish() {
        List<Future<Void>> futures = new LinkedList<>();
        Map<Integer, List<OutboxEvent>> events = repository.findForSend(BATCH_SIZE * partitionsNumber).stream()
                .collect(Collectors.groupingBy(e -> Math.floorMod(e.getKey().hashCode(), partitionsNumber)));
        for (int s = 0; s < partitionsNumber; s++) {
            List<OutboxEvent> shard = events.getOrDefault(s, List.of());
            KafkaTemplate<String, String> kafkaTemplate = kafkaTemplates.get(s);
            if (!shard.isEmpty()) {
                shard = shard.subList(0, Math.min(BATCH_SIZE, shard.size()));
                futures.add(outboxWorker.runOnce(shard, kafkaTemplate));
            }
        }
        for (Future<Void> f : futures) {
            try {
                f.get();
            } catch (Exception e) {
                log.warn("Kafka publication failed", e);
            }
        }
    }
}
