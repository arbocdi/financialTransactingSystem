package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.msg.Message;

public interface KeyExtractor {
    String getKey(Message message);
}
