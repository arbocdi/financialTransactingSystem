package kg.arbocdi.fts.test.simple;

import kg.arbocdi.fts.test.helpers.ApiHelper;
import kg.arbocdi.fts.test.helpers.ConcurrencyHelper;
import lombok.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateTransfers {
    private final ApiHelper apiHelper;
    private final ConcurrencyHelper concurrencyHelper;

    public Result createTransfers(int threads, List<Pair> pairs) {
        Result result = new Result();
        List<List<Pair>> portions = concurrencyHelper.split(pairs, threads);
        List<ConcurrencyHelper.Action> actions = portions.stream()
                .map(portion -> (ConcurrencyHelper.Action) () -> createTransfers(portion))
                .toList();

        concurrencyHelper.runConcurrently(result, actions);
        return result;
    }

    /**
     * Transfers 55 from-> to in 10 iterations
     *
     * @param pairs
     * @return
     */
    private Result createTransfers(List<Pair> pairs) {
        Result result = new Result();
        for (Pair pair : pairs) {
            for (int i = 0; i < 10; i++) {
                try {
                    apiHelper.transfer(pair.getFrom(), pair.getTo(), i + 1);
                    result.incrementSuccess();
                } catch (Exception e) {
                    result.incrementFail();
                }
            }
        }
        return result;
    }

    @Data
    public static class Pair {
        private final UUID from;
        private final UUID to;
    }

    @Getter
    @Setter
    @ToString(callSuper = true)
    public static class Result extends ConcurrencyHelper.Result {
        public Result() {
            super("CreateTransfers");
        }
    }
}
