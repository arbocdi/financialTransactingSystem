package kg.arbocdi.fts.transfers;

import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.compensateWithrdaw.CompensateWithdrawAccountCommand;
import kg.arbocdi.fts.api.accounts.deposit.DepositAccountCommand;
import kg.arbocdi.fts.api.accounts.withdraw.WithdrawAccountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class AccountCommandRestAdapter implements AccountCommandPort {
    private final RestClient restClient;
    @Value("${accounts.base-url}")
    private String accountsBaseUrl;

    @Override
    public void deposit(DepositAccountCommand cmd) {
        restClient.post()
                .uri(accountsBaseUrl + "/deposit")
                .contentType(APPLICATION_JSON)
                .body(cmd)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void withdraw(WithdrawAccountCommand cmd) {
        restClient.post()
                .uri(accountsBaseUrl + "/withdraw")
                .contentType(APPLICATION_JSON)
                .body(cmd)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void compensate(CompensateWithdrawAccountCommand cmd) {
        restClient.post()
                .uri(accountsBaseUrl + "/compensateWithdraw")
                .contentType(APPLICATION_JSON)
                .body(cmd)
                .retrieve()
                .toBodilessEntity();
    }
}
