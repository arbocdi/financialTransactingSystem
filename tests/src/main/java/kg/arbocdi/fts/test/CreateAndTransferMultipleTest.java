package kg.arbocdi.fts.test;

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
public class CreateAndTransferMultipleTest {
    private final ApiHelper apiHelper;

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
            apiHelper.createAccount(owner, account1);
            result.incrementCreatedAccounts();
            apiHelper.deposit(account1, 55);
            apiHelper.createAccount(owner, account2);
            result.incrementCreatedAccounts();

            for (int i = 1; i <= 9; i++) {
                try {
                    apiHelper.transfer(account1, account2, i);
                    result.incrementSuccess();
                } catch (BusinessException e) {
                    log.warn("Transfer failed", e);
                    result.incrementFail();
                }
            }
        }
        return result;
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
