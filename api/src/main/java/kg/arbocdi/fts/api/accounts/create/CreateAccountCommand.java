package kg.arbocdi.fts.api.accounts.create;

import kg.arbocdi.fts.api.accounts.AccountCommand;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString(callSuper = true)
public class CreateAccountCommand extends AccountCommand {
    private UUID ownerId;

}
