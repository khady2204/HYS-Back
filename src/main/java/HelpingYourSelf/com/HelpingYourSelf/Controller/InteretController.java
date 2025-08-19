package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.DTO.UserInteretRequest;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.InteretRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/interets")
@RequiredArgsConstructor
public class InteretController {

    private final InteretRepository interetRepository;
    private final UserRepository userRepository;

    // ✅ POST /api/interets — Ajouter un intérêt
    @PostMapping
    public ResponseEntity<Interet> createInteret(@RequestBody Interet interet) {
        Interet saved = interetRepository.save(interet);
        return ResponseEntity.ok(saved);
    }

    // ✅ GET /api/interets — Liste rapide test
    @GetMapping
    public ResponseEntity<List<Interet>> getAll() {
        return ResponseEntity.ok(interetRepository.findAll());
    }


    // ✅ GET /api/listeinterets — alias personnalisé
    @GetMapping("/listeinterets")
    public ResponseEntity<List<Interet>> listeInterets() {
        return ResponseEntity.ok(interetRepository.findAll());
    }

    // POST /api/interets/user — Enregistrer les intérêts choisis par l'utilisateur
    @PostMapping("/user")
    public ResponseEntity<String> saveUserInterets(@RequestBody UserInteretRequest request) {
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Utilisateur non trouvé");
        }
        User user = userOpt.get();

        List<Interet> interets = interetRepository.findAllById(request.getInteretIds());
        user.setInterets(interets);

        userRepository.save(user);

        return ResponseEntity.ok("Intérêts enregistrés avec succès");
    }

    // GET /api/interets/user/actuel — Récupérer les intérêts de l'utilisateur connecté
    @GetMapping("/user/actuel")
    public ResponseEntity<List<Interet>> getCurrentUserInterets() {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof HelpingYourSelf.com.HelpingYourSelf.Security.CustomUserDetails)) {
            return ResponseEntity.status(401).build();
        }

        HelpingYourSelf.com.HelpingYourSelf.Security.CustomUserDetails userDetails = (HelpingYourSelf.com.HelpingYourSelf.Security.CustomUserDetails) principal;
        Long userId = userDetails.getUser().getId();

        // Récupérer l'utilisateur avec ses intérêts initialisés
        java.util.Optional<HelpingYourSelf.com.HelpingYourSelf.Entity.User> userOpt = userRepository.findByIdWithInterets(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        HelpingYourSelf.com.HelpingYourSelf.Entity.User user = userOpt.get();
        List<Interet> interets = user.getInterets();

        return ResponseEntity.ok(interets);
    }
}
