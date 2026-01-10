package kg.arbocdi.fts.core.inbox;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class InboxEventsService {
    private final InboxMessagesRepository repository;

    public boolean tryInsert(UUID eventId) {
        return repository.tryInsert("inbox_events", eventId);
    }
}
