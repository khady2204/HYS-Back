package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestion")
@RequiredArgsConstructor
public class ManagerController {
    private final UserRepository userRepo;

    @GetMapping("/users")
    public List<User> listUsers() {

        return userRepo.findByRolesContaining(Role.USER);
    }

    @PostMapping("/block-user/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        User u = userRepo.findById(id).orElseThrow();
        u.setEnabled(false);
        userRepo.save(u);
        return ResponseEntity.ok("Compte bloqué");
    }

    @PostMapping("/unblock-user/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        User u = userRepo.findById(id).orElseThrow();
        u.setEnabled(true);
        userRepo.save(u);
        return ResponseEntity.ok("Compte débloqué");
    }
}

