package kg.arbocdi.fts.api.accounts.compensateWithrdaw;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class AccountWithdrawCompensatedEvent extends AccountEvent {
    private int amount;

    public AccountWithdrawCompensatedEvent(CompensateWithdrawAccountCommand cmd) {
        amount = cmd.getAmount();
    }
}
