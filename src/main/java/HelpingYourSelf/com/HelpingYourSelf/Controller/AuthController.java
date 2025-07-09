package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import HelpingYourSelf.com.HelpingYourSelf.DTO.LoginRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import java.util.Collections;

@CrossOrigin(origins = "http://localhost:8100")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        auth.register(request);
        return ResponseEntity.ok(Map.of("message", "Inscription réussie"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest http) {
        String token = auth.login(req, http.getRemoteAddr());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpLoginRequest req) {
        try {
            String otp = auth.sendOtp(req);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "OTP envoyé avec succès.");
            response.put("otp", otp); // À supprimer en production pour la sécurité
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Échec de l’envoi de l’OTP."));
        }
    }


    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpVerifyRequest req, HttpServletRequest request) {
        try {
            String token = auth.verifyOtp(req, request.getRemoteAddr());
            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP vérifié avec succès");
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", "OTP invalide ou expiré."));
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
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            System.out.println("Token à invalider : " + token);
        }
        return ResponseEntity.ok("Déconnexion réussie.");
    }



}
