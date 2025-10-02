package com.argenischacon.inventory_sales_api.config;

import com.argenischacon.inventory_sales_api.model.Role;
import com.argenischacon.inventory_sales_api.model.User;
import com.argenischacon.inventory_sales_api.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataLoader {
    @Bean
    CommandLineRunner load(UserRepository repo, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("admin123"));
                adminUser.setRoles(Set.of(Role.ADMIN, Role.USER));
                repo.save(adminUser);
            }
            if (repo.findByUsername("user").isEmpty()) {
                User regularUser = new User();
                regularUser.setUsername("user");
                regularUser.setPassword(passwordEncoder.encode("user123"));
                regularUser.setRoles(Set.of(Role.USER));
                repo.save(regularUser);
            }
        };
    }
}
