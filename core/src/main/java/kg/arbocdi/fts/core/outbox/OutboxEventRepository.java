package kg.arbocdi.fts.core.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Types;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class OutboxEventRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public void save(OutboxEvent event) {
        jdbcTemplate.update(
                """
                            INSERT INTO outbox_events (event_id,key,topic, payload)
                            VALUES (:event_id,:key,:topic,CAST(:payload AS jsonb))
                        """,
                new MapSqlParameterSource()
                        .addValue("event_id", event.getEventId(), Types.OTHER)
                        .addValue("key", event.getKey())
                        .addValue("topic", event.getTopic())
                        .addValue("payload", event.getPayload())
        );
    }

    public List<OutboxEvent> findForSend(int limit) {
        return jdbcTemplate.query("""
                            SELECT event_id,key,topic, payload, seq_number
                            FROM outbox_events
                            ORDER BY seq_number
                            LIMIT :limit
                        """, new MapSqlParameterSource()
                        .addValue("limit", limit),
                ROW_MAPPER);
    }

    public void deleteAll(List<OutboxEvent> events) {
        if (events.isEmpty()) return;

        var ids = events.stream().map(OutboxEvent::getEventId).toList();

        jdbcTemplate.update("""
                    DELETE FROM outbox_events
                    WHERE event_id IN (:ids)
                """, new MapSqlParameterSource("ids", ids));
    }

    private static final RowMapper<OutboxEvent> ROW_MAPPER = (rs, rowNum) -> new OutboxEvent(
            rs.getObject("event_id", UUID.class),
            rs.getString("key"),
            rs.getString("topic"),
            rs.getString("payload"),
            rs.getLong("seq_number")
    );
}
