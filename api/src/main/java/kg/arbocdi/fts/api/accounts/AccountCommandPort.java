package kg.arbocdi.fts.api.accounts;

import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;

/**
 * abstraction for future remote proxies
 */
public interface AccountCommandPort {


    void deposit(DepositAccountCommand cmd);

    void withdraw(WithdrawAccountCommand cmd);

    void compensate(CompensateWithdrawAccountCommand cmd);

}