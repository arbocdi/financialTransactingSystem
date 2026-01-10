package kg.arbocdi.fts.core.inbox;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class InboxMessagesRepository {

    private final JdbcTemplate jdbcTemplate;

    public boolean tryInsert(String table, UUID messageId) {
        int inserted = jdbcTemplate.update("""
                    INSERT INTO %s(message_id)
                    VALUES (?)
                    ON CONFLICT DO NOTHING
                """.formatted(table), messageId);

        return inserted == 1;
    }
}
