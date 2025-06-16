package HelpingYourSelf.com.HelpingYourSelf.Controller;

import org.springframework.web.bind.annotation.RequestBody;
import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
/*
    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpLoginRequest req) {
        auth.sendOtp(req);
        return ResponseEntity.ok("OTP envoyé");
    }
*/

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody OtpLoginRequest req) {
        String otp = auth.sendOtp(req);
        return ResponseEntity.ok("Code OTP (test) : " + otp);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verify(@RequestBody OtpVerifyRequest req, HttpServletRequest http) {
        String token = auth.verifyOtp(req, http.getRemoteAddr());
        return ResponseEntity.ok(Collections.singletonMap("token", token));
    }

    /*

    @PostMapping("/reset")
    public ResponseEntity<?> reset(@RequestBody ResetRequest req) {
        auth.sendResetOtp(req);
        return ResponseEntity.ok("OTP réinitialisation envoyé");
    }
    */

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


