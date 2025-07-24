package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.NewPasswordRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.RegisterRequest;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/superadmin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    private final AuthService auth;

    // Création d’un gestionnaire par le SuperAdmin
    @PostMapping("/create-manager")
    public ResponseEntity<?> createManager(@RequestBody RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas");
        }

        if (userRepo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }

        User manager = new User();
        manager.setEmail(req.getEmail());
        manager.setNom(req.getNom());
        manager.setPhone(req.getPhone());
        manager.setPassword(encoder.encode(req.getPassword()));
        manager.setRoles(Set.of(Role.GESTIONNAIRE));
        manager.setEnabled(true);

        userRepo.save(manager);
        return ResponseEntity.ok("Gestionnaire créé avec succès");
    }


    //  Liste des gestionnaires
    @GetMapping("/managers")
    public ResponseEntity<?> listManagers() {
        List<User> managers = userRepo.findByRolesContaining(Role.GESTIONNAIRE);
        return ResponseEntity.ok(managers);
    }


    //  Réinitialiser le mot de passe d’un gestionnaire
    @PostMapping("/reset-manager-password/{managerId}")
    public ResponseEntity<?> resetManagerPassword(@PathVariable Long managerId,
                                                  @RequestBody NewPasswordRequest req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas");
        }

        User manager = userRepo.findById(managerId)
                .orElseThrow(() -> new RuntimeException("Gestionnaire introuvable"));

        manager.setPassword(encoder.encode(req.getNewPassword()));
        userRepo.save(manager);

        return ResponseEntity.ok("Mot de passe du gestionnaire mis à jour avec succès");
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/superadmin/create")
    public ResponseEntity<?> createSuperAdmin(@RequestBody RegisterRequest req) {
        try {
            auth.createSuperAdmin(req);
            return ResponseEntity.ok("Nouveau superadmin créé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
