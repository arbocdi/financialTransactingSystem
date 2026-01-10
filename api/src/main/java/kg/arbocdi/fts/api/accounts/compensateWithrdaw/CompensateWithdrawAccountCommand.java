package kg.arbocdi.fts.api.accounts.compensateWithrdaw;

import kg.arbocdi.fts.api.accounts.AccountCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class CompensateWithdrawAccountCommand extends AccountCommand {
    private int amount;
}
