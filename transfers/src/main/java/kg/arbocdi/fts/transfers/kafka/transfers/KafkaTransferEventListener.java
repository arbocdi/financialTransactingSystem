package kg.arbocdi.fts.transfers.kafka.transfers;

import kg.arbocdi.fts.api.transfers.TransferEvent;
import kg.arbocdi.fts.core.inbox.InboxEventsService;
import kg.arbocdi.fts.transfers.TransferSagaService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class KafkaTransferEventListener {
    private final InboxEventsService inboxEventsService;
    private final TransferSagaService transferSagaService;
    private final JsonMapper mapper;

    @KafkaListener(topics = "transfer-events",groupId = "transfer-sagas")
    @Transactional
    public void on(String payload) {
        TransferEvent event = mapper.readValue(payload, TransferEvent.class);
        if(!inboxEventsService.tryInsert(event.getMessageId())) return;
        transferSagaService.onEvent(event);
    }

    // обработчик DLT:
    @Transactional
    public void onDlt(String payload) {
        TransferEvent event = mapper.readValue(payload, TransferEvent.class);
        if(!inboxEventsService.tryInsert(event.getMessageId())) return;
        transferSagaService.onEvent(event);
    }
}
