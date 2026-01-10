package kg.arbocdi.fts.transfers;

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
        TransferApplication.class,
        ConfigMarker.class,
        InboxMarker.class,
        RestMarker.class,
        ExternalPortsMarker.class,
        OutboxMarker.class,
        OutboxKafkaMarker.class
})
@EnableScheduling
public class TransferApplication {
    public static void main(String[] args) {
        SpringApplication.run(TransferApplication.class, args);
    }
}
