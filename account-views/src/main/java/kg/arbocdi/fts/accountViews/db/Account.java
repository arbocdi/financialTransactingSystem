package kg.arbocdi.fts.accountViews.db;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.create.AccountCreatedEvent;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;
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

    public void on(AccountCreatedEvent evt) {
        id = evt.getAggregateId();
        ownerId = evt.getOwnerId();
    }

    public void on(AccountDepositedEvent evt) {
        balance += evt.getAmount();
    }

    public void on(AccountWithdrawnEvent evt) {
        balance -= evt.getAmount();
    }

    public void on(AccountWithdrawCompensatedEvent evt) {
        balance += evt.getAmount();
    }
}
