package kg.arbocdi.fts.core.inbox;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class InboxCommandsService {
    private final InboxMessagesRepository repository;
    public boolean tryInsert(UUID commandId) {
        return repository.tryInsert("inbox_commands", commandId);
    }
}
