package kg.arbocdi.fts.accountViews.api;

import kg.arbocdi.fts.accountViews.db.Account;
import kg.arbocdi.fts.accountViews.redis.AccountsRedisRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class AccountsController {
    private final AccountsRedisRepo repo;

    @GetMapping("/owner/{ownerId}")
    public List<Account> getAccounts(@PathVariable UUID ownerId) {
        return repo.getAccounts(ownerId);
    }

    @GetMapping("/owner/{ownerId}/account/{accountId}")
    public Account getAccount(@PathVariable UUID ownerId, @PathVariable UUID accountId) {
        return repo.getAccount(ownerId, accountId);
    }
}
