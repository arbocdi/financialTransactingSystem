package kg.arbocdi.fts.accounts.api;

import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.CreateAccountPort;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountCommandPort accountCommandPort;
    private final CreateAccountPort createAccountPort;
    @PostMapping("")
    public void create(@RequestBody CreateAccountCommand cmd) {
        createAccountPort.create(cmd);
    }
    @PostMapping("/withdraw")
    public void withdraw(@RequestBody WithdrawAccountCommand cmd) {
        accountCommandPort.withdraw(cmd);
    }
    @PostMapping("/compensateWithdraw")
    public void compensateWithdraw(@RequestBody CompensateWithdrawAccountCommand cmd) {
        accountCommandPort.compensate(cmd);
    }
    @PostMapping("/deposit")
    public void deposit(@RequestBody DepositAccountCommand cmd) {
        accountCommandPort.deposit(cmd);
    }
}
