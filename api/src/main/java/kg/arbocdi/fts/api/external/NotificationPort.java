package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.accounts.AccountEvent;

public interface NotificationPort {
    void notify(AccountEvent event);
}
