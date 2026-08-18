package com.mahalak.media.config;

import com.mahalak.media.entity.Role;
import com.mahalak.media.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RoleBootstrapConfig {

    @Bean
    @ConditionalOnProperty(name = "app.bootstrap.roles-enabled", havingValue = "true", matchIfMissing = true)
    CommandLineRunner seedRoles(RoleRepository roleRepository) {
        return args -> List.of("ADMIN", "STAFF", "CUSTOMER").forEach(roleName ->
                roleRepository.findByRole(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(null, roleName))));
    }
}
