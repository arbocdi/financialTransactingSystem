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
                            INSERT INTO outbox_events (event_id, payload,state)
                            VALUES (:event_id,CAST(:payload AS jsonb),:state)
                        """,
                new MapSqlParameterSource()
                        .addValue("event_id", event.getEventId(), Types.OTHER)
                        .addValue("payload", event.getPayload())
                        .addValue("state", event.getState().name())
        );
    }

    public void delete(OutboxEvent event) {
        jdbcTemplate.update("""
                    DELETE FROM outbox_events
                    WHERE event_id = :event_id
                """, new MapSqlParameterSource("event_id", event.getEventId())
        );
    }

    public void updateState(OutboxEvent event) {
        jdbcTemplate.update("""
                    update outbox_events
                    set state = :state
                    WHERE event_id = :event_id
                """, new MapSqlParameterSource()
                .addValue("event_id", event.getEventId())
                .addValue("state", event.getState().name())
        );
    }

    public List<OutboxEvent> findForSend(int limit) {
        return jdbcTemplate.query("""
                    SELECT event_id, payload, seq_number,state
                    FROM outbox_events
                    WHERE state = 'NEW'
                    ORDER BY seq_number
                    LIMIT :limit
                """, new MapSqlParameterSource("limit", limit), ROW_MAPPER);
    }

    public void deleteAll(List<OutboxEvent> events) {
        if (events.isEmpty()) return;

        var ids = events.stream().map(OutboxEvent::getEventId).toList();

        jdbcTemplate.update("""
                    DELETE FROM outbox_events
                    WHERE event_id IN (:ids)
                """, new MapSqlParameterSource("ids", ids));
    }

    public void markSent(List<OutboxEvent> events) {
        if (events.isEmpty()) return;

        var ids = events.stream().map(OutboxEvent::getEventId).toList();

        jdbcTemplate.update("""
                    UPDATE outbox_events
                    SET state = 'SENT'
                    WHERE event_id IN (:ids)
                """, new MapSqlParameterSource("ids", ids));
    }

    private static final RowMapper<OutboxEvent> ROW_MAPPER = (rs, rowNum) -> new OutboxEvent(
            rs.getObject("event_id", UUID.class),
            rs.getString("payload"),
            OutboxEvent.State.valueOf(rs.getString("state")),
            rs.getLong("seq_number")
    );
}
