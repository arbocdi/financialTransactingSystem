package kg.arbocdi.fts.core.outbox;

import kg.arbocdi.fts.core.msg.Message;
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
    private String payload;
    private State state = State.NEW;
    private long seqNumber;

    public OutboxEvent(Message event, ObjectMapper om) {
        eventId = event.getMessageId();
        payload = om.writerWithDefaultPrettyPrinter().writeValueAsString(event);
    }

    public Message getPayloadAsMessage(ObjectMapper om) {
        return om.readValue(payload, Message.class);
    }

    public enum State {
        NEW, SENT
    }
}
