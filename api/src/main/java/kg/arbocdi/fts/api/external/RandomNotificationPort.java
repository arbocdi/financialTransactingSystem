package kg.arbocdi.fts.api.external;

import kg.arbocdi.fts.api.accounts.AccountEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RandomNotificationPort implements NotificationPort{
    private final RandomUtils randomUtils;
    @Override
    public void notify(AccountEvent event) {
        if (randomUtils.isError(30)) {
            throw new RuntimeException("Notification could not be processed");
        }
    }
}
