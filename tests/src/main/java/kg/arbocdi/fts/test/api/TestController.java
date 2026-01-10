package kg.arbocdi.fts.test.api;

import kg.arbocdi.fts.test.CreateAndTransferTest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final CreateAndTransferTest createAndTransferTest;

    @PostMapping("/create-and-transfer")
    public CreateAndTransferTest.Result createAndTransfer(
            @RequestParam("threads") int threads,
            @RequestParam("accounts") int accounts
    ) throws InterruptedException {
        return createAndTransferTest.createAccountsAndTransfer(threads, accounts);
    }
}
