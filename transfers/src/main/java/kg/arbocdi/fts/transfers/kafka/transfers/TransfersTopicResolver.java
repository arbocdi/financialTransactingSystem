package kg.arbocdi.fts.transfers.kafka.transfers;

import kg.arbocdi.fts.api.transfers.TransferEvent;
import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox_kafka.TopicResolver;
import org.springframework.stereotype.Component;

@Component
public class TransfersTopicResolver implements TopicResolver {
    @Override
    public String getTopic(Message message) {
        if (message instanceof TransferEvent) {
            return "transfer-events";
        }
        throw new IllegalArgumentException("Unknown message type");
    }
}
