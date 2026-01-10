package kg.arbocdi.fts.core.msg.aggregate;

import kg.arbocdi.fts.core.msg.Message;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class AggregateMessage extends Message {
    private UUID aggregateId;

}
