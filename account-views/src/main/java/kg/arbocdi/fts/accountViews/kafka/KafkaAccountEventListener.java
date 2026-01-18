package kg.arbocdi.fts.accountViews.kafka;

import kg.arbocdi.fts.accountViews.AccountService;
import kg.arbocdi.fts.accountViews.db.Account;
import kg.arbocdi.fts.accountViews.redis.AccountsRedisRepo;
import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.core.inbox.InboxEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class KafkaAccountEventListener {
    private final InboxEventsService inboxEventsService;
    private final AccountService accountService;
    private final JsonMapper mapper;
    private final TransactionOperations transactionOperations;
    private final AccountsRedisRepo accountsRedisRepo;

    @KafkaListener(topics = "account-events", groupId = "account-views")
    public void on(String payload) {
        AccountEvent event = mapper.readValue(payload, AccountEvent.class);
        Account account = transactionOperations.execute(status -> {
            if (!inboxEventsService.tryInsert(event.getMessageId())) return null;
            return accountService.on(event);
        });
        if (account != null) accountsRedisRepo.upsertAccount(account);
    }
}
