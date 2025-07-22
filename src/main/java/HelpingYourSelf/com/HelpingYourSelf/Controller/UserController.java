package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicUserDTO;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UpdateProfileRequest;
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

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final InteretRepository interetRepository;


    //  Liste des utilisateurs (ROLE_ADMIN ou GESTIONNAIRE)
    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {

        List<User> users = userRepo.findByRolesContaining(Role.USER);
        return ResponseEntity.ok(users);
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
                user.getAdresse()
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
            @AuthenticationPrincipal User currentUser,
            @RequestBody UpdateProfileRequest request
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        // Mise à jour des champs de base
        currentUser.setNom(request.getNom());
        currentUser.setPrenom(request.getPrenom());
        currentUser.setEmail(request.getEmail());
        currentUser.setAdresse(request.getAdresse());
        currentUser.setBio(request.getBio());
        currentUser.setProfileImage(request.getProfileImage());

        // Mise à jour des centres d’intérêt si fournis


        userRepo.save(currentUser);
        return ResponseEntity.ok("Profil mis à jour avec succès");
    }


}
