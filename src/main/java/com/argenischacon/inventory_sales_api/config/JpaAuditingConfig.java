package com.argenischacon.inventory_sales_api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.envers.repository.config.EnableEnversRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@EnableEnversRepositories(basePackages = "com.argenischacon.inventory_sales_api.repository")
@ConditionalOnProperty (prefix = "app.jpa", name = "auditing-enabled", havingValue = "true", matchIfMissing = true)
public class JpaAuditingConfig {
}
