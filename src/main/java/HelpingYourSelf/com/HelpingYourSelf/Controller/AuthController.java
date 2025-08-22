package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;

import HelpingYourSelf.com.HelpingYourSelf.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import HelpingYourSelf.com.HelpingYourSelf.DTO.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;



import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8100")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;
    private final UserRepository userRepo;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        auth.register(request);
        return ResponseEntity.ok("Inscription réussie");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest http) {
        String token = auth.login(req, http.getRemoteAddr());

        // Mise à jour de isOnline = true selon que l’utilisateur utilise email ou téléphone
        Optional<User> optionalUser = Optional.empty();

        if (req.getPhone() != null && !req.getPhone().isEmpty()) {
            optionalUser = userRepo.findByPhone(req.getPhone());
        } else if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            optionalUser = userRepo.findByEmail(req.getEmail());
        }

        optionalUser.ifPresent(user -> {
            user.setIsOnline(true);
            userRepo.save(user);
        });

        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMonProfil(@AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(userService.getMonProfil(user));
    }


    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpLoginRequest req) {
        String otp = auth.sendOtp(req);
        return ResponseEntity.ok("Code OTP (test) : " + otp);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest req, HttpServletRequest http) {
        String token = auth.verifyOtp(req, http.getRemoteAddr());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }




    // Étape 1 : Demande d’OTP
    @PostMapping("/reset/request")
    public ResponseEntity<?> requestResetOtp(@RequestBody ResetRequest req) {
        String otp = auth.sendResetOtp(req);
        return ResponseEntity.ok("OTP envoyé pour réinitialisation");
    }

    // Étape 2 : Vérification du code OTP
    @PostMapping("/reset/verify-otp")
    public ResponseEntity<?> verifyResetOtp(@RequestBody VerifyOtpRequest req) {
        try {
            auth.verifyResetOtp(req);
            return ResponseEntity.ok("OTP vérifié avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Étape 3 : Réinitialisation du mot de passe
    @PostMapping("/reset/password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetConfirmRequest req) {
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Les mots de passe ne correspondent pas");
        }

        try {
            auth.confirmReset(req);
            return ResponseEntity.ok("Mot de passe réinitialisé avec succès");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal(expression = "user") User user) {
        user.setIsOnline(false);
        userRepo.save(user);
        return ResponseEntity.ok("Déconnexion réussie");
    }


}
