package HelpingYourSelf.com.HelpingYourSelf.Security;

import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void createDefaultSuperAdmin() {
        boolean exists = userRepository.findByEmail("superadmin@helpingyourself.com").isPresent();
        if (!exists) {
            User user = new User();
            user.setNom("Super");
            user.setPrenom("Admin");
            user.setEmail("superadmin@helpingyourself.com");
            user.setPassword(passwordEncoder.encode("Password123"));
            user.setPhone("0000000000");

            user.setRoles(Set.of(Role.valueOf("SUPERADMIN")));
            user.setEnabled(true);
            userRepository.save(user); 
            System.out.println(" SuperAdmin créé par défaut");
        } else {
            System.out.println(" SuperAdmin déjà présent");
        }
    }
}
