package kg.arbocdi.fts.test.api;

import kg.arbocdi.fts.test.CreateAndTransferMultipleTest;
import kg.arbocdi.fts.test.CreateAndTransferSingleTest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final CreateAndTransferMultipleTest createAndTransferMultipleTest;
    private final CreateAndTransferSingleTest createAndTransferSingleTest;

    @PostMapping("/create-and-transfer-multiple")
    public CreateAndTransferMultipleTest.Result createAndTransferMultiple(
            @RequestParam("threads") int threads,
            @RequestParam("accounts") int accounts
    ) throws InterruptedException {
        return createAndTransferMultipleTest.createAccountsAndTransfer(threads, accounts);
    }

    @PostMapping("/create-and-transfer-single")
    public CreateAndTransferSingleTest.Result createAndTransferSingle(
    ) throws InterruptedException {
        return createAndTransferSingleTest.createAccountsAndTransfer();
    }
}
