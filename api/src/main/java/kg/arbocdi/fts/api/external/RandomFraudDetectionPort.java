package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomFraudDetectionPort implements FraudDetectionPort {
    private final RandomUtils randomUtils;

    @Override
    public void check(CreateTransferCommand cmd) {
        if (randomUtils.isError(20)) {
            // throw new FraudDetectedException();
        }
    }
}
