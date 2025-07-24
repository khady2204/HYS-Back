package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicUserDTO;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UpdateProfileRequest;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.InteretRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final InteretRepository interetRepository;


    //  Liste des utilisateurs
    @GetMapping("/list")
    public ResponseEntity<?> listUsersByRole(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        Set<Role> roles = currentUser.getRoles();

        if (roles.contains(Role.SUPERADMIN)) {
            // Le SUPERADMIN voit tous les types d'utilisateurs
            return ResponseEntity.ok(Map.of(
                    "admins", userRepo.findByRolesContaining(Role.SUPERADMIN),
                    "gestionnaires", userRepo.findByRolesContaining(Role.GESTIONNAIRE),
                    "utilisateurs", userRepo.findByRolesContaining(Role.USER)
            ));
        }

        if (roles.contains(Role.GESTIONNAIRE)) {
            // Le GESTIONNAIRE voit les utilisateurs et les autres gestionnaires
            return ResponseEntity.ok(Map.of(
                    "gestionnaires", userRepo.findByRolesContaining(Role.GESTIONNAIRE),
                    "utilisateurs", userRepo.findByRolesContaining(Role.USER)
            ));
        }

        if (roles.contains(Role.USER)) {
            // L’utilisateur voit uniquement les autres utilisateurs
            return ResponseEntity.ok(userRepo.findByRolesContaining(Role.USER));
        }

        return ResponseEntity.status(403).body("Accès refusé");
    }




    //  Bloquer/Débloquer un utilisateur par un gestionnaire
    @PutMapping("/block/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_GESTIONNAIRE')")
    public ResponseEntity<?> toggleBlock(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setEnabled(!user.isEnabled());
        userRepo.save(user);
        return ResponseEntity.ok("Utilisateur " + (user.isEnabled() ? "débloqué" : "bloqué"));
    }


    //  Récupérer le profil de l'utilisateur
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        PublicUserDTO publicDTO = new PublicUserDTO(
                user.getPrenom(),
                user.getNom(),
                user.getEmail(),
                user.getAdresse(),
                user.getProfileImage()
        );

        return ResponseEntity.ok(publicDTO);
    }



    //  Lister les utilisateurs connectés (isOnline = true)
    @GetMapping("/online")
    public ResponseEntity<?> getOnlineUsers() {
        List<User> onlineUsers = userRepo.findByIsOnlineTrue();
        return ResponseEntity.ok(onlineUsers);
    }

    @PutMapping("/update-profile")
    @PreAuthorize("hasAnyAuthority('ROLE_USER', 'ROLE_ADMIN', 'ROLE_GESTIONNAIRE')")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal(expression = "user") User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        currentUser.setNom(request.getNom());
        currentUser.setPrenom(request.getPrenom());
        currentUser.setEmail(request.getEmail());
        currentUser.setPhone(request.getPhone());
        currentUser.setAdresse(request.getAdresse());
        currentUser.setBio(request.getBio());
        currentUser.setProfileImage(request.getProfileImage());

        if (request.getInteretIds() != null && !request.getInteretIds().isEmpty()) {
            List<Interet> interets = interetRepository.findAllById(request.getInteretIds());
            currentUser.setInterets(interets);
        }

        userRepo.save(currentUser);
        return ResponseEntity.ok("Profil mis à jour avec succès");
    }



}
