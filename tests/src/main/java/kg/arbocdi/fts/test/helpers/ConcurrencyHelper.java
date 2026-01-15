package kg.arbocdi.fts.test.helpers;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
public class ConcurrencyHelper {

    public void runConcurrently(Result result, List<Action> actions) {
        long start = System.currentTimeMillis();
        List<Thread> tasks = new LinkedList<>();
        for (Action action : actions) {
            Thread t = new Thread(() -> merge(result, action.run()));
            tasks.add(t);
            t.start();
        }
        try {
            for (Thread t : tasks) {
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        result.setTime(end - start);
    }

    public void runConcurrently(int threads, Action action, Result result) {
        long start = System.currentTimeMillis();
        List<Thread> tasks = new LinkedList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> merge(result, action.run()));
            tasks.add(t);
            t.start();
        }
        try {
            for (Thread t : tasks) {
                t.join();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        result.setTime(end - start);
    }

    private void merge(Result base, Result other) {
        synchronized (base) {
            base.merge(other);
        }
    }

    public <T> List<List<T>> split(List<T> list, int portions) {
        int portionSize = list.size() / portions;
        List<T> portion = new ArrayList<>(portionSize);
        List<List<T>> result = new ArrayList<>();
        for (T item : list) {
            portion.add(item);
            if (portion.size() == portionSize) {
                result.add(portion);
                portion = new ArrayList<>(portionSize);
            }
        }
        return result;
    }

    public interface Action {
        Result run();
    }

    @Data
    public static class Result {
        private long time;
        private int successCount;
        private int failCount;
        private String name;

        public Result(String name) {
            this.name = name;
        }

        public void incrementSuccess() {
            successCount++;
        }

        public void incrementFail() {
            failCount++;
        }

        public double getRps() {
            if (time == 0) return 0D;
            return (successCount + failCount) / (double) time * 1000;
        }

        public void merge(Result other) {
            this.successCount += other.successCount;
            this.failCount += other.failCount;
            this.time += other.time;
        }
    }
}
