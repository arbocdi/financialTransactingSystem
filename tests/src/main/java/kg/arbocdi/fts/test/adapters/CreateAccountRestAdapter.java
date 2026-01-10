package kg.arbocdi.fts.test.adapters;

import kg.arbocdi.fts.api.accounts.CreateAccountPort;
import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class CreateAccountRestAdapter implements CreateAccountPort {
    private final RestClient restClient;
    @Value("${accounts.base-url}")
    private String accountsBaseUrl;

    @Override
    public void create(CreateAccountCommand cmd) {
        restClient.post()
                .uri(accountsBaseUrl)
                .contentType(APPLICATION_JSON)
                .body(cmd)
                .retrieve()
                .toBodilessEntity();
    }
}
