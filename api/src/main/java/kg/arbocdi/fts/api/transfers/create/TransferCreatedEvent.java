package kg.arbocdi.fts.api.transfers.create;

import kg.arbocdi.fts.api.transfers.TransferEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class TransferCreatedEvent extends TransferEvent {
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private int amount;

    public TransferCreatedEvent(CreateTransferCommand cmd) {
        applyPrev(cmd);
        sourceAccountId = cmd.getSourceAccountId();
        targetAccountId = cmd.getTargetAccountId();
        amount = cmd.getAmount();
    }
}
