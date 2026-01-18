package kg.arbocdi.fts.accountViews;

import jakarta.persistence.EntityManagerFactory;
import kg.arbocdi.fts.core.cfg.ConfigMarker;
import kg.arbocdi.fts.core.inbox.InboxMarker;
import kg.arbocdi.fts.core.rest.RestMarker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

@SpringBootApplication(scanBasePackageClasses = {
        AccountViewsApplication.class,
        ConfigMarker.class,
        InboxMarker.class,
        RestMarker.class,

})
@EnableScheduling
public class AccountViewsApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountViewsApplication.class, args);
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}
