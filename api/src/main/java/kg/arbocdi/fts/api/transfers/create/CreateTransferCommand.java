package kg.arbocdi.fts.api.transfers.create;

import kg.arbocdi.fts.api.transfers.TransferCommand;
import kg.arbocdi.fts.core.msg.Command;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class CreateTransferCommand extends TransferCommand {
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private int amount;
}
