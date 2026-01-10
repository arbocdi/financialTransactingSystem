package kg.arbocdi.fts.test.adapters;

import kg.arbocdi.fts.api.transfers.CreateTransferPort;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Component
@RequiredArgsConstructor
public class CreateTransferRestAdapter implements CreateTransferPort {
    private final RestClient restClient;
    @Value("${transfers.base-url}")
    private String transfersBaseUrl;

    @Override
    public void create(CreateTransferCommand cmd) {
        restClient.post()
                .uri(transfersBaseUrl)
                .contentType(APPLICATION_JSON)
                .body(cmd)
                .retrieve()
                .toBodilessEntity();
    }
}
