package app.user_service.seeder;

import app.user_service.entity.Role;
import app.user_service.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder {
    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER"));
        }

        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN"));
        }
    }
}
