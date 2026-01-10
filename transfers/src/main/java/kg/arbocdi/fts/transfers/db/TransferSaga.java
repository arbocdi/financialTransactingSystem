package kg.arbocdi.fts.transfers.db;

import jakarta.persistence.*;
import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.AccountWithdrawCompensatedEvent;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.AccountDepositedEvent;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.AccountWithdrawnEvent;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;
import kg.arbocdi.fts.api.transfers.TransferEvent;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import kg.arbocdi.fts.api.transfers.create.TransferCreatedEvent;
import kg.arbocdi.fts.core.EventPublisher;
import kg.arbocdi.fts.core.cfg.MessageJsonConverter;
import kg.arbocdi.fts.core.exception.BusinessException;
import kg.arbocdi.fts.core.msg.Message;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Data
@Entity
@Table(name = "transfer_sagas")
@NoArgsConstructor
public class TransferSaga {
    @Id
    private UUID id;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private int amount;
    @Enumerated(EnumType.STRING)
    private State state;
    private String lastError;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = MessageJsonConverter.class)
    private Message withdrawCmd;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = MessageJsonConverter.class)
    private Message compensateCmd;
    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = MessageJsonConverter.class)
    private Message depositCmd;

    private boolean inTerminalState;
    @Transient
    private EventPublisher eventPublisher;
    @Transient
    private AccountCommandPort accountCommandPort;

    public void on(CreateTransferCommand cmd) {
        this.id = cmd.getSagaId();
        this.sourceAccountId = cmd.getSourceAccountId();
        this.targetAccountId = cmd.getTargetAccountId();
        this.amount = cmd.getAmount();

        WithdrawAccountCommand withdrawCmd = new WithdrawAccountCommand();
        withdrawCmd.setAmount(amount);
        withdrawCmd.setAggregateId(sourceAccountId);
        withdrawCmd.applyPrev(cmd);
        this.withdrawCmd = withdrawCmd;

        CompensateWithdrawAccountCommand compensateCmd = new CompensateWithdrawAccountCommand();
        compensateCmd.setAmount(amount);
        compensateCmd.setAggregateId(sourceAccountId);
        compensateCmd.applyPrev(withdrawCmd);
        this.compensateCmd = compensateCmd;

        DepositAccountCommand depositCmd = new DepositAccountCommand();
        depositCmd.setAmount(amount);
        depositCmd.setAggregateId(targetAccountId);
        this.depositCmd = depositCmd;

        state = State.WITHDRAW_PENDING;
        eventPublisher.publish(new TransferCreatedEvent(cmd), cmd);
    }


    public void onEvent(TransferEvent event) {
        if (event.getClass() == TransferCreatedEvent.class) withdraw((TransferCreatedEvent) event);
    }

    private void withdraw(TransferCreatedEvent event) {
        if (state != State.WITHDRAW_PENDING) return;
        try {
            getWithdrawCmd().applyPrev(event);
            accountCommandPort.withdraw(getWithdrawCmd());
        } catch (BusinessException e) {
            setState(State.WITHDRAW_FAILED);
        }
    }


    public void onEvent(AccountEvent event) {
        if (event.getClass() == AccountWithdrawnEvent.class)
            withdrawCompleted((AccountWithdrawnEvent) event);
        if (event.getClass() == AccountDepositedEvent.class) depositCompleted((AccountDepositedEvent) event);
        if (event.getClass() == AccountWithdrawCompensatedEvent.class)
            compensated((AccountWithdrawCompensatedEvent) event);
        inTerminalState = state.isTerminalState();
    }

    private void withdrawCompleted(AccountWithdrawnEvent event) {
        if (state != State.WITHDRAW_PENDING) return;
        setState(State.WITHDRAW_COMPLETED);
        getDepositCmd().applyPrev(event);
        deposit();
    }

    private void deposit() {
        if (state != State.WITHDRAW_COMPLETED) return;
        setState(State.DEPOSIT_PENDING);
        try {
            accountCommandPort.deposit(getDepositCmd());
        } catch (BusinessException e) {
            setState(State.DEPOSIT_FAILED);
            compensate();
        }
    }

    private void depositCompleted(AccountDepositedEvent event) {
        if (state != State.DEPOSIT_PENDING) return;
        setState(State.DEPOSIT_COMPLETED);
    }

    private void compensate() {
        if (state != State.DEPOSIT_FAILED) return;
        setState(State.COMPENSATION_PENDING);
        try {
            accountCommandPort.compensate(getCompensateCmd());
        } catch (BusinessException e) {
            setState(State.COMPENSATION_FAILED);
        }
    }

    private void compensated(AccountWithdrawCompensatedEvent event) {
        if (state != State.COMPENSATION_PENDING) return;
        setState(State.COMPENSATED);
    }

    public enum State {
        WITHDRAW_PENDING(false), WITHDRAW_COMPLETED(false), WITHDRAW_FAILED(true),
        DEPOSIT_PENDING(false), DEPOSIT_COMPLETED(true), DEPOSIT_FAILED(false),
        COMPENSATION_PENDING(false), COMPENSATED(true), COMPENSATION_FAILED(true);
        @Getter
        private final boolean terminalState;

        State(boolean terminalState) {
            this.terminalState = terminalState;
        }

    }

    public WithdrawAccountCommand getWithdrawCmd() {
        return (WithdrawAccountCommand) this.withdrawCmd;
    }

    public DepositAccountCommand getDepositCmd() {
        return (DepositAccountCommand) this.depositCmd;
    }

    public CompensateWithdrawAccountCommand getCompensateCmd() {
        return (CompensateWithdrawAccountCommand) this.compensateCmd;
    }
}
