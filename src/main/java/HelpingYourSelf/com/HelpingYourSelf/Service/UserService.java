package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByRole(String role) {
        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            return userRepository.findByRolesContaining(roleEnum);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rôle invalide : " + role);
        }
    }


    public List<User> getUsersCreatedBy(User gestionnaire) {
        return userRepository.findByCreatedBy(gestionnaire);
    }

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    public void blockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBlocked(true);
            userRepository.save(user);
        });
    }

    public void unblockUser(Long id) {
        userRepository.findById(id).ifPresent(user -> {
            user.setBlocked(false);
            userRepository.save(user);
        });
    }

    public boolean resetPasswordAsSuperadmin(Long userId, String newPassword) {
        return userRepository.findById(userId).map(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }).orElse(false);
    }
}
