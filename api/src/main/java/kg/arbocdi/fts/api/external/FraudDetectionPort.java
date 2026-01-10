package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import kg.arbocdi.fts.core.exception.BusinessException;

public interface FraudDetectionPort {
    void check(CreateTransferCommand cmd);

    class FraudDetectedException extends BusinessException {
        public FraudDetectedException() {
            super("FraudDetected!", "fraud.deteced");
        }
    }
}
