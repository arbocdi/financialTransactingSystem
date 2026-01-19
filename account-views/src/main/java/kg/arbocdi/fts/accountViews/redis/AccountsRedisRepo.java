package kg.arbocdi.fts.accountViews.redis;

import kg.arbocdi.fts.accountViews.db.Account;
import kg.arbocdi.fts.accountViews.db.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.time.Duration;
import java.util.*;

@Component
@RequiredArgsConstructor
public class AccountsRedisRepo {

    private final StringRedisTemplate redis;
    private final JsonMapper mapper;
    private final AccountRepository repository;

    // TTL на owner hash
    private final Duration ownerTtl = Duration.ofMinutes(10);


    /**
     * write-through: обновить/вставить аккаунт в HASH
     */
    public void upsertAccount(Account account) {
        String key = ownerAccountsKey(account.getOwnerId());
        redis.opsForHash().put(key, account.getId().toString(), mapper.writeValueAsString(account));
        // TTL на весь hash (перезаписываем, чтобы не протух)
        redis.expire(key, ownerTtl);
    }

    public void removeAccount(Account account) {
        String key = ownerAccountsKey(account.getOwnerId());
        redis.opsForHash().delete(key, account.getId().toString());
    }

    public Account getAccount(UUID ownerId, UUID accountId) {
        String key = ownerAccountsKey(ownerId);
        String value = (String) redis.opsForHash().get(key, accountId.toString());
        if (value == null) return null;
        return mapper.readValue(value, Account.class);
    }

    /**
     * Получить все аккаунты owner.
     * 1) пробуем HGETALL
     * 2) если пусто -> грузим из БД (все аккаунты owner), прогреваем hash и возвращаем
     */
    public List<Account> getAccounts(UUID ownerId) {
        String key = ownerAccountsKey(ownerId);

        Map<Object, Object> map = redis.opsForHash().entries(key);
        if (map != null && !map.isEmpty()) {
            return map.values().stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .map(s -> mapper.readValue(s, Account.class))
                    .toList();
        }

        // miss -> грузим из БД
        List<Account> accounts = repository.findByOwnerId(ownerId);
        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }

        // прогрев (лучше пайплайном)
        Map<String, String> toPut = new HashMap<>(accounts.size());
        List<Account> result = new ArrayList<>(accounts.size());

        for (Account a : accounts) {
            String json = mapper.writeValueAsString(a);
            toPut.put(a.getId().toString(), json);
            result.add(a);
        }

        // одним вызовом кладём всё в HASH
        redis.opsForHash().putAll(key, toPut);
        redis.expire(key, ownerTtl);

        return result;
    }

    private String ownerTag(UUID ownerId) {
        //hash-tag для Redis Cluster (всё owner-специфичное на одной ноде)
        //node = hash({...})%N
        return "owner:{" + ownerId + "}";
    }

    private String ownerAccountsKey(UUID ownerId) {
        return ownerTag(ownerId) + ":accounts";
    }
}




