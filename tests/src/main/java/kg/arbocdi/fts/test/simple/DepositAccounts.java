package kg.arbocdi.fts.test.simple;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kg.arbocdi.fts.test.helpers.ApiHelper;
import kg.arbocdi.fts.test.helpers.ConcurrencyHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DepositAccounts {
    private final ApiHelper apiHelper;
    private final ConcurrencyHelper concurrencyHelper;

    public Result depositAccounts(int threads, List<UUID> accounts, int sum) {
        Result result = new Result();
        List<List<UUID>> portions = concurrencyHelper.split(accounts, threads);
        List<ConcurrencyHelper.Action> actions = portions.stream()
                .map(portion -> (ConcurrencyHelper.Action) () -> depositAccounts(portion, sum))
                .toList();

        concurrencyHelper.runConcurrently(result, actions);
        return result;
    }

    private Result depositAccounts(List<UUID> accounts, int sum) {
        Result result = new Result();
        for (UUID account : accounts) {
            try {
                apiHelper.deposit(account, sum);
                result.incrementSuccess();
                result.addDepositedAccount(account);
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
        private List<UUID> depositedAccounts = new LinkedList<>();

        public Result() {
            super("DepositAccounts");
        }

        public void addDepositedAccount(UUID account) {
            depositedAccounts.add(account);
        }

        public void merge(ConcurrencyHelper.Result other) {
            super.merge(other);
            Result otherResult = (Result) other;
            this.depositedAccounts.addAll(otherResult.depositedAccounts);
        }
    }
}
