package kg.arbocdi.fts.accounts.kafka;

import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.msg.aggregate.AggregateMessage;
import kg.arbocdi.fts.core.outbox_kafka.KeyExtractor;
import org.springframework.stereotype.Component;

@Component
public class AggregateIdKeyExtractor implements KeyExtractor {
    @Override
    public String getKey(Message message) {
        if(message instanceof AggregateMessage){
            return ((AggregateMessage) message).getAggregateId().toString();
        }
        throw new IllegalArgumentException("Unknown message type");
    }
}
