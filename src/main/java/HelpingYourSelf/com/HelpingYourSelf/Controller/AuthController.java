package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import HelpingYourSelf.com.HelpingYourSelf.DTO.LoginRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService auth;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        auth.register(request);
        return ResponseEntity.ok("Inscription réussie");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest http) {
        String token = auth.login(req, http.getRemoteAddr());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
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

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetRequest req) {
        String otp = auth.sendResetOtp(req);
        return ResponseEntity.ok("OTP reset (test) : " + otp);
    }

    @PostMapping("/reset/confirm")
    public ResponseEntity<?> confirm(@RequestBody ResetConfirmRequest req) {
        auth.confirmReset(req);
        return ResponseEntity.ok("Mot de passe réinitialisé");
    }
}
