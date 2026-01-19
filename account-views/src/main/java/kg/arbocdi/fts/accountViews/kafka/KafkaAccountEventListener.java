package kg.arbocdi.fts.accountViews.kafka;

import jakarta.persistence.EntityManager;
import kg.arbocdi.fts.accountViews.AccountService;
import kg.arbocdi.fts.accountViews.db.Account;
import kg.arbocdi.fts.accountViews.redis.AccountsRedisRepo;
import kg.arbocdi.fts.api.accounts.AccountEvent;
import kg.arbocdi.fts.core.inbox.InboxEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.retry.RetryException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

@Component
@RequiredArgsConstructor
public class KafkaAccountEventListener {
    private final InboxEventsService inboxEventsService;
    private final AccountService accountService;
    private final JsonMapper mapper;
    private final AccountsRedisRepo accountsRedisRepo;
    private final EntityManager entityManager;

    @KafkaListener(topics = "account-events", groupId = "account-views")
    @Transactional
    public void on(String payload) throws RetryException {
        AccountEvent event = mapper.readValue(payload, AccountEvent.class);
        if (!inboxEventsService.tryInsert(event.getMessageId())) return;
        Account account = accountService.on(event);
        entityManager.flush();
        if (account != null) accountsRedisRepo.upsertAccount(account);
    }
}
