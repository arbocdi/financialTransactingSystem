package kg.arbocdi.fts.test;

import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.CreateAccountPort;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.transfers.CreateTransferPort;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import kg.arbocdi.fts.core.exception.BusinessException;
import kg.arbocdi.fts.core.util.UUIDGenerator;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateAndTransferTest {
    private final CreateAccountPort createAccountPort;
    private final AccountCommandPort accountCommandPort;
    private final CreateTransferPort createTransferPort;

    public Result createAccountsAndTransfer(int threads, int accountCount) throws InterruptedException {
        Result result = new Result();
        List<Thread> tasks = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> result.merge(createAccountsAndTransfer(accountCount)));
            tasks.add(t);
            t.start();
        }
        for (Thread t : tasks) {
            t.join();
        }
        return result;
    }

    private Result createAccountsAndTransfer(int accountCount) {
        Result result = new Result();
        for (int a = 1; a <= accountCount; a++) {
            UUID owner = UUIDGenerator.generate();
            UUID account1 = UUIDGenerator.generate();
            UUID account2 = UUIDGenerator.generate();
            createAccount(owner, account1);
            result.incrementCreatedAccounts();
            deposit(account1, 55);
            createAccount(owner, account2);
            result.incrementCreatedAccounts();

            for (int i = 1; i <= 9; i++) {
                try {
                    transfer(account1, account2, i);
                    result.incrementSuccess();
                } catch (BusinessException e) {
                    log.warn("Transfer failed", e);
                    result.incrementFail();
                }
            }
        }
        return result;
    }

    private void createAccount(UUID ownerId, UUID accountId) {
        CreateAccountCommand cmd = new CreateAccountCommand();
        cmd.setAggregateId(accountId);
        cmd.setOwnerId(ownerId);
        createAccountPort.create(cmd);
    }

    private void deposit(UUID accountId, int amount) {
        DepositAccountCommand cmd = new DepositAccountCommand();
        cmd.setAggregateId(accountId);
        cmd.setAmount(amount);
        accountCommandPort.deposit(cmd);
    }

    private void transfer(UUID from, UUID to, int amount) {
        CreateTransferCommand cmd = new CreateTransferCommand();
        cmd.setSagaId(UUIDGenerator.generate());
        cmd.setSourceAccountId(from);
        cmd.setTargetAccountId(to);
        cmd.setAmount(amount);
        createTransferPort.create(cmd);
    }

    @Data
    public static class Result {
        private int successCount;
        private int failCount;
        private int createdAccounts;

        public void incrementSuccess() {
            successCount++;
        }

        public void incrementFail() {
            failCount++;
        }

        public void incrementCreatedAccounts() {
            createdAccounts++;
        }

        public synchronized void merge(Result other) {
            this.successCount += other.successCount;
            this.failCount += other.failCount;
            this.createdAccounts += other.createdAccounts;
        }
    }
}
