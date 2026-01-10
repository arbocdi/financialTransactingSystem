package kg.arbocdi.fts.transfers.kafka.transfers;

import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox_kafka.KeyExtractor;
import org.springframework.stereotype.Component;

@Component
public class SagaIdKeyExtractor implements KeyExtractor {
    @Override
    public String getKey(Message message) {
        return message.getSagaId().toString();
    }
}
