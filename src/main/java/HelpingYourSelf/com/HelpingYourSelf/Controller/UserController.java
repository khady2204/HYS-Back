package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicUserDTO;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UpdateProfileRequest;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UpdateProfileResponse;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UserSummary;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.InteretRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;
    private final InteretRepository interetRepository;
    private final JwtTokenProvider jwtTokenProvider;

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

        ZoneId zone = ZoneId.of("Africa/Dakar");
        String label = Boolean.TRUE.equals(user.getIsOnline())
                ? null
                : humanizeLastOnline(user.getLastOnlineAt(), zone);

        PublicUserDTO dto = new PublicUserDTO(
                user.getPrenom(),
                user.getNom(),
                user.getEmail(),
                user.getAdresse(),
                user.getProfileImage(),
                Boolean.TRUE.equals(user.getIsOnline()),
                user.getLastOnlineAt(),
                label
        );

        return ResponseEntity.ok(dto);
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
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String bio,
            @RequestParam(required = false) String adresse,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) List<Long> interetIds
    ) {
        if (currentUser == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }

        if (prenom != null) currentUser.setPrenom(prenom);
        if (nom != null) currentUser.setNom(nom);
        if (email != null) currentUser.setEmail(email);
        if (phone != null) currentUser.setPhone(phone);
        if (adresse != null) currentUser.setAdresse(adresse);
        if (bio != null) currentUser.setBio(bio);

        if (profileImage != null && !profileImage.isEmpty()) {
            String imageUrl = saveProfileImage(profileImage);
            currentUser.setProfileImage(imageUrl);
        }

        if (interetIds != null && !interetIds.isEmpty()) {
            List<Interet> interets = interetRepository.findAllById(interetIds);
            currentUser.setInterets(interets);
        }

        userRepo.save(currentUser);


        String newToken = jwtTokenProvider.generateTokenFromUser(currentUser);

        UserSummary summary = new UserSummary(
                currentUser.getId(),
                currentUser.getNom(),
                currentUser.getPrenom(),
                currentUser.getEmail(),
                currentUser.getPhone(),
                currentUser.getAdresse(),
                currentUser.getBio(),
                currentUser.getProfileImage()
        );

        return ResponseEntity.ok(new UpdateProfileResponse(newToken, summary));
    }




    private String saveProfileImage(MultipartFile file) {
        try {
            String uploadDir = "uploads/profiles/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/profiles/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de la photo", e);
        }
    }



    @PostMapping("/{id}/follow")
    public ResponseEntity<?> follow(@PathVariable Long id, @AuthenticationPrincipal(expression = "user") User user) {
        User toFollow = userRepo.findById(id).orElseThrow();
        toFollow.getFollowers().add(user);
        userRepo.save(toFollow);
        return ResponseEntity.ok("Abonnement réussi.");
    }

    @PostMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollow(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "user") User user) {

        User toUnfollow = userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        toUnfollow.getFollowers().remove(user);
        userRepo.save(toUnfollow);

        return ResponseEntity.ok("Désabonnement réussi.");
    }

    @GetMapping("/followers")
    public ResponseEntity<Set<User>> getMesFollowers(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(user.getFollowers());
    }

    @GetMapping("/abonnements")
    public ResponseEntity<Set<User>> getMesAbonnements(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(user.getAbonnements());
    }





    private String humanizeLastOnline(Instant ts, ZoneId zone) {
        if (ts == null) return null;
        ZonedDateTime zdt = ts.atZone(zone);
        LocalDate date = zdt.toLocalDate();
        LocalDate today = LocalDate.now(zone);

        if (date.equals(today)) {
            return "Aujourd’hui " + zdt.toLocalTime().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        } else if (date.equals(today.minusDays(1))) {
            return "Hier " + zdt.toLocalTime().truncatedTo(java.time.temporal.ChronoUnit.MINUTES);
        } else {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm").withZone(zone);
            return fmt.format(zdt);
        }
    }




}
