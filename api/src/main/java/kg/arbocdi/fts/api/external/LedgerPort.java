package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;

public interface LedgerPort {
    void recordWithdrawal(AccountWithdrawnEvent event);
    void recordDeposit(AccountWithdrawCompensatedEvent event);
    void recordWithdrawCompensation(AccountDepositedEvent event);
}
