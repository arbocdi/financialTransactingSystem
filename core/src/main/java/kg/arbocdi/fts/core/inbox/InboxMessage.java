package kg.arbocdi.fts.core.inbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * should be on the same shard as aggregate
 */
public class InboxMessage {
    private UUID messageId;
}
