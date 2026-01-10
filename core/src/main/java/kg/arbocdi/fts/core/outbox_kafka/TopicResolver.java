package kg.arbocdi.fts.core.outbox_kafka;

import kg.arbocdi.fts.core.msg.Message;

public interface TopicResolver {
    String getTopic(Message message);
}
