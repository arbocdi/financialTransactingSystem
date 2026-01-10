package kg.arbocdi.fts.core;

import kg.arbocdi.fts.core.msg.Command;
import kg.arbocdi.fts.core.msg.Event;
import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.msg.aggregate.AggregateCommand;
import kg.arbocdi.fts.core.msg.aggregate.AggregateEvent;

public interface EventPublisher {
    void publish(Message event);

    default void publish(AggregateEvent event, AggregateCommand cmd) {
        event.applyCommand(cmd);
        publish(event);
    }

    default void publish(Event event, Command cmd) {
        event.applyPrev(cmd);
        publish(event);
    }
}
