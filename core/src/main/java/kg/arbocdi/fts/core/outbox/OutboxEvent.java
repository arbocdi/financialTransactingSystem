package kg.arbocdi.fts.core.outbox;

import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox_kafka.KeyExtractor;
import kg.arbocdi.fts.core.outbox_kafka.TopicResolver;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OutboxEvent {
    private UUID eventId;
    private String key;
    private String topic;
    private String payload;
    private long seqNumber;

    public OutboxEvent(Message event, ObjectMapper om, KeyExtractor keyExtractor, TopicResolver topicResolver) {
        eventId = event.getMessageId();
        key = keyExtractor.getKey(event);
        topic = topicResolver.getTopic(event);
        payload = om.writerWithDefaultPrettyPrinter().writeValueAsString(event);
    }

    public Message getPayloadAsMessage(ObjectMapper om) {
        return om.readValue(payload, Message.class);
    }
}
