package ecom.jcommerce.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// Without this, @CreatedDate / @LastModifiedDate stay null and the NOT NULL
// constraints on created_at / updated_at reject the INSERT.
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}
