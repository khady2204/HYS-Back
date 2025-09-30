package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;

import HelpingYourSelf.com.HelpingYourSelf.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import HelpingYourSelf.com.HelpingYourSelf.DTO.LoginRequest;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;



import java.time.Instant;
import java.util.*;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;
    private final UserRepository userRepo;
    private final UserService userService;
    private final JavaMailSender javaMailSender;
    private final Environment environment;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        auth.register(request);
        // ✅ Retourner du JSON
        Map<String, String> response = new HashMap<>();
        response.put("message", "Inscription réussie");
        response.put("status", "success");
        return ResponseEntity.ok(response);
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

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> payload) {
        String idToken = payload.get("idToken");
        try {
            String token = auth.processGoogleToken(idToken);
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
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
        if (user != null) {
            user.setIsOnline(false);
            user.setLastOnlineAt(Instant.now());
            userRepo.save(user);
        }
        return ResponseEntity.ok("Déconnecté avec succès.");
    }

    @GetMapping("/debug-mail-config")
    public Map<String, String> debugMailConfig() {
        return Map.of(
                "spring.mail.host", environment.getProperty("spring.mail.host", "non défini"),
                "spring.mail.username", environment.getProperty("spring.mail.username", "non défini"),
                "spring.mail.from", environment.getProperty("spring.mail.from", "non défini"),
                "app.email.from", environment.getProperty("app.email.from", "non défini"),
                "java.version", System.getProperty("java.version"),
                "activeProfiles", String.join(", ", environment.getActiveProfiles())
        );
    }

    @GetMapping("/debug-smtp-detail")
    public ResponseEntity<?> debugSmtpDetail() {
        try {
            Map<String, String> config = new HashMap<>();
            config.put("mail.host", environment.getProperty("spring.mail.host"));
            config.put("mail.from", environment.getProperty("spring.mail.from"));
            config.put("app.email.from", environment.getProperty("app.email.from"));

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(environment.getProperty("spring.mail.from", "default@example.com"));
            message.setTo("segnanelaye@gmail.com");
            message.setSubject("Debug SMTP - " + new Date());
            message.setText("Configuration: " + config.toString());

            javaMailSender.send(message);
            return ResponseEntity.ok("Email envoyé avec config: " + config);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("Erreur: " + e.getMessage() + "\nConfig: " + getMailConfig());
        }
    }

    private Map<String, String> getMailConfig() {
        Map<String, String> config = new HashMap<>();
        for (String key : Arrays.asList(
                "spring.mail.host", "spring.mail.port", "spring.mail.username",
                "spring.mail.from", "app.email.from", "spring.profiles.active"
        )) {
            config.put(key, environment.getProperty(key, "non défini"));
        }
        return config;
    }

}
