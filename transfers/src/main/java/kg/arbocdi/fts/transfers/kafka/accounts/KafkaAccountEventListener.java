package kg.arbocdi.fts.transfers.kafka.accounts;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.core.inbox.InboxEventsService;
import kg.arbocdi.fts.transfers.TransferSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class KafkaAccountEventListener {
    private final InboxEventsService inboxEventsService;
    private final TransferSagaService transferSagaService;
    private final JsonMapper mapper;

    @KafkaListener(topics = "account-events", groupId = "transfer-sagas")
    @Transactional
    public void on(String payload) {
        AccountEvent event = mapper.readValue(payload, AccountEvent.class);
        if (!inboxEventsService.tryInsert(event.getMessageId())) return;
        transferSagaService.onEvent(event);
    }

    // обработчик DLT:
    @Transactional
    public void onDlt(String payload) {
        AccountEvent event = mapper.readValue(payload, AccountEvent.class);
        if (!inboxEventsService.tryInsert(event.getMessageId())) return;
        transferSagaService.onEvent(event);
    }
}
