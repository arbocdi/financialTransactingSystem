package kg.arbocdi.fts.test.complex;

import kg.arbocdi.fts.core.util.UUIDGenerator;
import kg.arbocdi.fts.test.helpers.ApiHelper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateAndTransferSingleTest {
    private final ApiHelper apiHelper;

    public Result createAccountsAndTransfer() {
        Result result = new Result();
        UUID owner = UUIDGenerator.generate();
        UUID account1 = UUIDGenerator.generate();
        UUID account2 = UUIDGenerator.generate();
        apiHelper.createAccount(owner, account1);
        result.incrementCreatedAccounts();
        apiHelper.deposit(account1, 55);
        apiHelper.createAccount(owner, account2);
        result.incrementCreatedAccounts();
        apiHelper.transfer(account1, account2, 45);
        result.incrementSuccess();
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
