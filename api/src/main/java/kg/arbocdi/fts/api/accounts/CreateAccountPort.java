package kg.arbocdi.fts.api.accounts;

import kg.arbocdi.fts.api.accounts.create.CreateAccountCommand;

public interface CreateAccountPort {
    void create(CreateAccountCommand cmd);
}
