package kg.arbocdi.fts.api.accounts.deposit;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class AccountDepositedEvent extends AccountEvent {
    private int amount;

    public AccountDepositedEvent(DepositAccountCommand cmd) {
        amount = cmd.getAmount();
    }
}
