package kg.arbocdi.fts.transfers.api;

import kg.arbocdi.fts.api.transfers.CreateTransferPort;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransfersController {
    private final CreateTransferPort createTransferPort;
    @PostMapping("")
    public void createTransfer(@RequestBody CreateTransferCommand cmd) {
        createTransferPort.create(cmd);
    }
}
