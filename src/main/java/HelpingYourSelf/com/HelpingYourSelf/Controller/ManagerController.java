package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gestion")
@RequiredArgsConstructor
public class ManagerController {
    private final UserService userService; // Remplace UserRepository

    @GetMapping("/users")
    public List<User> listUsers() {
        return userService.getUsersByRole("USER"); // Utilise le service
    }

    @PostMapping("/block-user/{id}")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        userService.blockUser(id); // Utilise le service
        return ResponseEntity.ok("Compte bloqué");
    }

    @PostMapping("/unblock-user/{id}")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        userService.unblockUser(id); // Utilise le service
        return ResponseEntity.ok("Compte débloqué");
    }
}