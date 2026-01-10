package kg.arbocdi.fts.accounts;

import kg.arbocdi.fts.api.external.ExternalPortsMarker;
import kg.arbocdi.fts.core.cfg.ConfigMarker;
import kg.arbocdi.fts.core.inbox.InboxMarker;
import kg.arbocdi.fts.core.outbox.OutboxMarker;
import kg.arbocdi.fts.core.outbox_kafka.OutboxKafkaMarker;
import kg.arbocdi.fts.core.rest.RestMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackageClasses = {
        AccountsApplication.class,
        ConfigMarker.class,
        OutboxMarker.class,
        RestMarker.class,
        OutboxKafkaMarker.class,
        InboxMarker.class
})
@EnableScheduling
public class AccountsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }
}
