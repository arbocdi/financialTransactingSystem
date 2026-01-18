package kg.arbocdi.fts.accountViews;

import kg.arbocdi.fts.accountViews.db.Account;
import kg.arbocdi.fts.accountViews.db.AccountRepository;
import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.create.AccountCreatedEvent;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account on(AccountEvent e) {
        if (e.getClass() == AccountCreatedEvent.class) return on((AccountCreatedEvent) e);
        else if (e.getClass() == AccountDepositedEvent.class) return deposit((AccountDepositedEvent) e);
        else if (e.getClass() == AccountWithdrawnEvent.class) return withdraw((AccountWithdrawnEvent) e);
        else if (e.getClass() == AccountWithdrawCompensatedEvent.class)
            return compensate((AccountWithdrawCompensatedEvent) e);
        return null;
    }

    private Account on(AccountCreatedEvent evt) {
        Optional<Account> accountOpt = accountRepository.findByIdLocked(evt.getAggregateId());
        if (accountOpt.isPresent()) return accountOpt.get();
        Account account = new Account();
        account.on(evt);
        return accountRepository.save(account);
    }

    private Account deposit(AccountDepositedEvent e) {
        return on(e, account -> account.on(e));
    }

    private Account withdraw(AccountWithdrawnEvent e) {
        return on(e, account -> account.on(e));
    }

    private Account compensate(AccountWithdrawCompensatedEvent e) {
        return on(e, account -> account.on(e));
    }

    private Account on(AccountEvent event, Consumer<Account> consumer) {
        Optional<Account> account = accountRepository.findByIdLocked(event.getAggregateId());
        if (account.isEmpty()) return null;
        consumer.accept(account.get());
        return account.get();
    }
}
