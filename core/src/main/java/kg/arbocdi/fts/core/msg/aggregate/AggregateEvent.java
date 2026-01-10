package kg.arbocdi.fts.core.msg.aggregate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class AggregateEvent extends AggregateMessage {
    public void applyCommand(AggregateCommand cmd) {
        super.applyPrev(cmd);
        setAggregateId(cmd.getAggregateId());
    }
}
