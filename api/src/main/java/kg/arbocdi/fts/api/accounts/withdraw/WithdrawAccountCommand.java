package kg.arbocdi.fts.api.accounts.withdraw;

import kg.arbocdi.fts.api.accounts.AccountCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class WithdrawAccountCommand extends AccountCommand {
    private int amount;
}
