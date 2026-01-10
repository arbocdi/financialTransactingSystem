package kg.arbocdi.fts.transfers;

import kg.arbocdi.fts.api.accounts.AccountCommandPort;
import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.api.external.FraudDetectionPort;
import kg.arbocdi.fts.api.transfers.CreateTransferPort;
import kg.arbocdi.fts.api.transfers.TransferEvent;
import kg.arbocdi.fts.api.transfers.create.CreateTransferCommand;
import kg.arbocdi.fts.core.EventPublisher;
import kg.arbocdi.fts.transfers.db.TransferSaga;
import kg.arbocdi.fts.transfers.db.TransferSagaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSagaService implements CreateTransferPort {

    private final TransferSagaRepository repository;
    private final AccountCommandPort accountCommandPort;
    private final FraudDetectionPort fraudDetectionPort;
    private final EventPublisher eventPublisher;

    @Transactional
    public void create(CreateTransferCommand cmd) {
        fraudDetectionPort.check(cmd);
        TransferSaga saga = new TransferSaga();
        saga.setEventPublisher(eventPublisher);
        saga.on(cmd);
        repository.save(saga);
    }

    @Transactional
    public void onEvent(TransferEvent event) {
        Optional<TransferSaga> sagaOpt = readSaga(event.getSagaId());
        if (sagaOpt.isEmpty()) return;
        TransferSaga saga = sagaOpt.get();
        saga.onEvent(event);
        repository.save(saga);
    }

    @Transactional
    public void onEvent(AccountEvent event) {
        Optional<TransferSaga> sagaOpt = readSaga(event.getSagaId());
        if (sagaOpt.isEmpty()) return;
        TransferSaga saga = sagaOpt.get();
        saga.onEvent(event);
        repository.save(saga);
    }

    private Optional<TransferSaga> readSaga(UUID id) {
        if (id == null) return Optional.empty();
        Optional<TransferSaga> sagaOpt = repository.findActiveForUpdate(id);
        sagaOpt.ifPresent(saga -> saga.setEventPublisher(eventPublisher));
        sagaOpt.ifPresent(saga -> saga.setAccountCommandPort(accountCommandPort));
        return sagaOpt;
    }

}
