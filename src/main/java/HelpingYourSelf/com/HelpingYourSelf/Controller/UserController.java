package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.UserService;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final UserService userService;

    // 🔹 Lister les utilisateurs ayant le rôle USER
    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTIONNAIRE')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepo.findByRolesContaining(Role.USER);
        return ResponseEntity.ok(users);
    }

    // 🔹 Bloquer / Débloquer un utilisateur
    @PutMapping("/block/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTIONNAIRE')")
    public ResponseEntity<?> toggleBlock(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setEnabled(!user.isEnabled());
        userRepo.save(user);
        return ResponseEntity.ok("Utilisateur " + (user.isEnabled() ? "débloqué" : "bloqué"));
    }

    // 🔹 Associer plusieurs intérêts à un utilisateur
    @PostMapping("/{userId}/interets")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> addInteretsToUser(@PathVariable Long userId, @RequestBody List<Long> interetIds) {
        userService.addInteretsToUser(userId, interetIds);
        return ResponseEntity.ok("Intérêts ajoutés avec succès à l'utilisateur ID=" + userId);
    }

    // 🔹 Associer un seul intérêt spécifique à un utilisateur
    @PostMapping("/{userId}/interets/{interetId}")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_SUPERADMIN')")
    public ResponseEntity<?> addSingleInteretToUser(@PathVariable Long userId, @PathVariable Long interetId) {
        userService.addInteretsToUser(userId, List.of(interetId));
        return ResponseEntity.ok("Intérêt ID=" + interetId + " ajouté à l'utilisateur ID=" + userId);
    }
}
