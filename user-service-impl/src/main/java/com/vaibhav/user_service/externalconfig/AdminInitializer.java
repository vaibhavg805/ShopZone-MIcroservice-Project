package com.vaibhav.user_service.externalconfig;
import com.vaibhav.user_service.entity.Role;
import com.vaibhav.user_service.entity.User;
import com.vaibhav.user_service.repository.RoleRepository;
import com.vaibhav.user_service.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createAdmin() {

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name("ROLE_ADMIN")
                                        .description("Administrator")
                                        .build()
                        )
                );

        // create admin user if not exists
        if (userRepository.findByUsername("admin").isPresent()) {
            return; // already created
        }

        Set<Role> roles = new HashSet<>();
        roles.add(adminRole);

        User admin = User.builder()
                .username("admin")
                .email("admin@mail.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("System")
                .lastName("Admin")
                .isActive(true)
                .roles(roles)
                .build();

        userRepository.save(admin);

        System.out.println("Default admin created");
    }
}

