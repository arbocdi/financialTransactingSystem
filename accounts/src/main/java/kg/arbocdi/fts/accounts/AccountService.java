package kg.arbocdi.fts.accounts;

import jakarta.transaction.Transactional;
import kg.arbocdi.fts.accounts.db.Account;
import kg.arbocdi.fts.accounts.db.AccountRepository;
import kg.arbocdi.fts.api.accounts.AccountCommand;
import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.CreateAccountPort;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;
import kg.arbocdi.fts.core.EventPublisher;
import kg.arbocdi.fts.core.exception.AggregateAlreadyExistsException;
import kg.arbocdi.fts.core.exception.AggregateNotFoundException;
import kg.arbocdi.fts.core.inbox.InboxCommandsService;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
public class AccountService implements CreateAccountPort,AccountCommandPort {
    private final AccountRepository accountRepository;
    private final EventPublisher eventPublisher;
    private final InboxCommandsService inboxCommandsService;

    public AccountService(AccountRepository accountRepository, EventPublisher eventPublisher, InboxCommandsService inboxCommandsService) {
        this.accountRepository = accountRepository;
        this.eventPublisher = eventPublisher;
        this.inboxCommandsService = inboxCommandsService;
    }

    @Transactional
    public void create(CreateAccountCommand cmd) {
        if (!inboxCommandsService.tryInsert(cmd.getMessageId())) return;
        accountRepository.findById(cmd.getAggregateId()).ifPresent(account ->
                {
                    throw new AggregateAlreadyExistsException();
                }
        );
        Account account = new Account();
        account.setEventPublisher(eventPublisher);
        account.on(cmd);
        accountRepository.save(account);
    }

    @Transactional
    public void deposit(DepositAccountCommand cmd) {
        on(cmd, account -> account.on(cmd));
    }

    @Transactional
    public void withdraw(WithdrawAccountCommand cmd) {
        on(cmd, account -> account.on(cmd));
    }

    @Transactional
    public void compensate(CompensateWithdrawAccountCommand cmd) {
        on(cmd, account -> account.on(cmd));
    }

    private void on(AccountCommand cmd, Consumer<Account> consumer) {
        Account account = accountRepository.findById(cmd.getAggregateId()).orElseThrow(AggregateNotFoundException::new);
        if (!inboxCommandsService.tryInsert(cmd.getMessageId())) return;
        account.setEventPublisher(eventPublisher);
        consumer.accept(account);
    }
}
