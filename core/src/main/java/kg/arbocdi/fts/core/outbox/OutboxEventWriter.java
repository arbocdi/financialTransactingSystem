package kg.arbocdi.fts.core.outbox;

import kg.arbocdi.fts.core.EventPublisher;
import kg.arbocdi.fts.core.msg.Message;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
@Component
public class OutboxEventWriter implements EventPublisher {
    private final OutboxEventRepository repository;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(Message event) {
        repository.save(new OutboxEvent(event, objectMapper));
    }
}
