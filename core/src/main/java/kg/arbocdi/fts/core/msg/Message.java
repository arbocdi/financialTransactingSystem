package kg.arbocdi.fts.core.msg;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import kg.arbocdi.fts.core.util.UUIDGenerator;
import lombok.Data;

import java.util.UUID;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.CLASS,
        include = JsonTypeInfo.As.PROPERTY,
        property = "@type"
)
public class Message {
    private UUID messageId = UUIDGenerator.generate();
    private UUID sagaId;
    /**
     * prev message id
     */
    private UUID causationIdId;
    /**
     * message id that started the process
     */
    private UUID rootCausationId;

    public void applyPrev(Message msg) {
        setSagaId(msg.getSagaId());
        setRootCausationId(msg.getRootCausationId());
        setCausationIdId(msg.getMessageId());
    }

}
