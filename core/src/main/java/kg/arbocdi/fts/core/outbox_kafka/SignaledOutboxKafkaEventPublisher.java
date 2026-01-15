package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.outbox.OutboxEvent;
import kg.arbocdi.fts.core.outbox.OutboxEventRepository;
import kg.arbocdi.fts.core.outbox.TraceContextRestorer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


@Component
@Slf4j
@RequiredArgsConstructor
public class SignaledOutboxKafkaEventPublisher implements InitializingBean, DisposableBean, Runnable {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository repository;
    private final ObjectProvider<TraceContextRestorer> traceContextRestorerObjectProvider;
    private final ObjectMapper objectMapper;
    private final Semaphore ready = new Semaphore(0);
    private Thread worker;
    private volatile boolean stopped;
    private static final int BATCH_SIZE = 200;


    @Override
    public void afterPropertiesSet() throws Exception {
        worker = new Thread(this);
        worker.start();
    }

    @Override
    public void destroy() throws Exception {
        this.stop();
    }

    public void run() {
        while (!stopped) {
            try {
                ready.tryAcquire(1, 200, TimeUnit.MILLISECONDS);
                ready.drainPermits();
            } catch (InterruptedException ex) {
                if (stopped) return;
            }
            try {
                List<OutboxEvent> events = repository.findForSend(BATCH_SIZE);
                kafkaTemplate.executeInTransaction(kt -> {
                    for (OutboxEvent e : events) {
                        TraceContextRestorer restorer = traceContextRestorerObjectProvider.getIfAvailable();
                        if (restorer != null)
                            restorer.withRestoredTrace(
                                    e.getHeadersMap(objectMapper),
                                    getSpanName(e),
                                    () -> kt.send(e.getTopic(), e.getKey(), e.getPayload())
                            );
                        else kt.send(e.getTopic(), e.getKey(), e.getPayload());
                    }
                    return true;
                });
                repository.deleteAll(events);
            } catch (Exception e) {
                log.warn("Kafka publication failed", e);
            }
        }

    }

    public void stop() {
        stopped = true;
        worker.interrupt();
    }

    public void signalReady() {
        ready.release();
    }

    private String getSpanName(OutboxEvent e) {
        return "outbox-event: " + e.getPayloadAsMessage(objectMapper).getClass().getSimpleName().replaceAll("\\.", "-");
    }

}
