package kg.arbocdi.fts.test.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.arbocdi.fts.core.util.UUIDGenerator;
import kg.arbocdi.fts.test.helpers.ApiHelper;
import kg.arbocdi.fts.test.helpers.ConcurrencyHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateAccounts {
    private final ApiHelper apiHelper;
    private final ConcurrencyHelper concurrencyHelper;

    public Result createAccounts(int threads, int accountsCount) {
        Result result = new Result();
        concurrencyHelper.runConcurrently(threads, () -> createAccounts(accountsCount / threads), result);
        return result;
    }

    private Result createAccounts(int accountCount) {
        Result result = new Result();
        UUID owner = UUIDGenerator.generate();
        for (int a = 0; a < accountCount; a++) {
            UUID account = UUIDGenerator.generate();
            try {
                apiHelper.createAccount(owner, account);
                result.incrementSuccess();
                result.addCreatedAccount(account);
            } catch (Exception e) {
                result.incrementFail();
            }
        }
        return result;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class Result extends ConcurrencyHelper.Result {
        @JsonIgnore
        private List<UUID> createdAccounts = new ArrayList<>();

        public Result() {
            super("CreateAccounts");
        }

        public void addCreatedAccount(UUID account) {
            createdAccounts.add(account);
        }

        public void merge(ConcurrencyHelper.Result other) {
            super.merge(other);
            Result otherResult = (Result) other;
            this.createdAccounts.addAll(otherResult.createdAccounts);
        }
    }
}
