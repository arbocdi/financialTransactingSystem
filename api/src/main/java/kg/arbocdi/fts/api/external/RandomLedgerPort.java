package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomLedgerPort implements LedgerPort {
    private final RandomUtils randomUtils;

    @Override
    public void recordWithdrawal(AccountWithdrawnEvent event) {
        randomError();
    }

    @Override
    public void recordDeposit(AccountWithdrawCompensatedEvent event) {
        randomError();
    }

    @Override
    public void recordWithdrawCompensation(AccountDepositedEvent event) {
        randomError();
    }

    private void randomError() {
        //if (randomUtils.isError(20)) throw new RuntimeException("Ledger could not be processed");
    }
}
