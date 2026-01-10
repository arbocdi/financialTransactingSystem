package kg.arbocdi.fts.api.accounts.deposit;

import kg.arbocdi.fts.api.accounts.AccountCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class DepositAccountCommand extends AccountCommand {
    private int amount;
}
