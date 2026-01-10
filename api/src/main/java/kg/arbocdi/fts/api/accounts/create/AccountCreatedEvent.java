package kg.arbocdi.fts.api.accounts.create;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
public class AccountCreatedEvent extends AccountEvent {
    private UUID ownerId;

    public AccountCreatedEvent(CreateAccountCommand cmd) {
        ownerId = cmd.getOwnerId();
    }
}
