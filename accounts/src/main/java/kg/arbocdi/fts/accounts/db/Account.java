package kg.arbocdi.fts.accounts.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import kg.arbocdi.fts.accounts.NotEnoughBalanceException;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.create.AccountCreatedEvent;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;
import kg.arbocdi.fts.core.EventPublisher;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Entity
@Table(name = "accounts")
@NoArgsConstructor
public class Account {
    @Id
    /*
      shard key
     */
    private UUID id;
    @NotNull
    private UUID ownerId;
    private int balance;
    @Transient
    private EventPublisher eventPublisher;

    public void on(CreateAccountCommand cmd) {
        id = cmd.getAggregateId();
        ownerId = cmd.getOwnerId();
        eventPublisher.publish(new AccountCreatedEvent(cmd), cmd);
    }

    public void on(DepositAccountCommand cmd) {
        balance += cmd.getAmount();
        eventPublisher.publish(new AccountDepositedEvent(cmd), cmd);
    }

    public void on(WithdrawAccountCommand cmd) {
        balance -= cmd.getAmount();
        if (balance < 0) throw new NotEnoughBalanceException();
        eventPublisher.publish(new AccountWithdrawnEvent(cmd), cmd);
    }

    public void on(CompensateWithdrawAccountCommand cmd) {
        balance += cmd.getAmount();
        eventPublisher.publish(new AccountWithdrawCompensatedEvent(cmd), cmd);
    }

}
