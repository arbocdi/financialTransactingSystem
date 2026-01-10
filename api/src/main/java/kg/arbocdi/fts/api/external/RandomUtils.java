package kg.arbocdi.fts.api.external;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomUtils {
    public boolean isError(int probabilityPercent) {
        int rnd = new Random(System.currentTimeMillis()).nextInt(100);
        return rnd < probabilityPercent - 1;
    }
}
