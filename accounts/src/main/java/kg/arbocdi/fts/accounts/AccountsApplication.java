package kg.arbocdi.fts.accounts;

import jakarta.persistence.EntityManagerFactory;
import kg.arbocdi.fts.core.cfg.ConfigMarker;
import kg.arbocdi.fts.core.inbox.InboxMarker;
import kg.arbocdi.fts.core.outbox.OutboxMarker;
import kg.arbocdi.fts.core.outbox_kafka.OutboxKafkaMarker;
import kg.arbocdi.fts.core.rest.RestMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
