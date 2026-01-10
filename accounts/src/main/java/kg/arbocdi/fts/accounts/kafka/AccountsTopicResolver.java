package kg.arbocdi.fts.accounts.kafka;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.core.msg.Message;
import kg.arbocdi.fts.core.outbox_kafka.TopicResolver;
import org.springframework.stereotype.Component;

@Component
public class AccountsTopicResolver implements TopicResolver {
    @Override
    public String getTopic(Message message) {
        if(message instanceof AccountEvent){
            return "account-events";
        }
        throw new IllegalArgumentException("Unknown message type");
    }
}
