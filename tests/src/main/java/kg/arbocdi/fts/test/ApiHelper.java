package kg.arbocdi.fts.test;

import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.CreateAccountPort;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.transfers.CreateTransferPort;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import kg.arbocdi.fts.core.util.UUIDGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ApiHelper {
    private final CreateAccountPort createAccountPort;
    private final AccountCommandPort accountCommandPort;
    private final CreateTransferPort createTransferPort;

    public void createAccount(UUID ownerId, UUID accountId) {
        CreateAccountCommand cmd = new CreateAccountCommand();
        cmd.setAggregateId(accountId);
        cmd.setOwnerId(ownerId);
        createAccountPort.create(cmd);
    }

    public void deposit(UUID accountId, int amount) {
        DepositAccountCommand cmd = new DepositAccountCommand();
        cmd.setAggregateId(accountId);
        cmd.setAmount(amount);
        accountCommandPort.deposit(cmd);
    }

    public void transfer(UUID from, UUID to, int amount) {
        CreateTransferCommand cmd = new CreateTransferCommand();
        cmd.setSagaId(UUIDGenerator.generate());
        cmd.setSourceAccountId(from);
        cmd.setTargetAccountId(to);
        cmd.setAmount(amount);
        createTransferPort.create(cmd);
    }
}
