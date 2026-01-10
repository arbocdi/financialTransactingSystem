package kg.arbocdi.fts.api.accounts.withdraw;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class AccountWithdrawnEvent extends AccountEvent {
    private int amount;

    public AccountWithdrawnEvent(WithdrawAccountCommand cmd) {
        amount = cmd.getAmount();
    }
}
