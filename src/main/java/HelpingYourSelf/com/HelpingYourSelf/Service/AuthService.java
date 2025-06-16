package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final TwilioService sms;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;

    public void register(RegisterRequest req) {
        User u = new User();
        u.setNom(req.getNom());
        u.setPhone(req.getPhone());
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEnabled(true);
        u.setRoles(Set.of("USER"));
        userRepo.save(u);
    }

    /*
    public void sendOtp(OtpLoginRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro non trouvé"));
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(code);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);
        sms.sendSms(user.getPhone(), "Votre code OTP est : " + code);
    }

     */

    public String sendOtp(OtpLoginRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro non trouvé"));

        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(code);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);

        //  Ne pas appeler Twilio en mode test
        // sms.sendSms(user.getPhone(), "Votre code OTP est : " + code);

        //  En mode test, on retourne l'OTP directement
        System.out.println(" [TEST] Code OTP pour " + user.getPhone() + " = " + code);
        return code;
    }



    public String verifyOtp(OtpVerifyRequest req, String ip) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalide"));
        if (user.getOtp() == null || !user.getOtp().equals(req.getOtp()))
            throw new RuntimeException("OTP incorrect");
        if (user.getOtpExpiration().isBefore(Instant.now()))
            throw new RuntimeException("OTP expiré");

        user.setOtp(null);
        user.setOtpExpiration(null);
        user.setLastLoginIp(ip);
        user.setDeviceInfo(req.getDeviceInfo());
        userRepo.save(user);

        return jwt.generateToken(user);
    }

    /*
    public void sendResetOtp(ResetRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro introuvable"));
        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);
        sms.sendSms(user.getPhone(), "OTP réinitialisation : " + otp);
    }
    */

    public String sendResetOtp(ResetRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro introuvable"));

        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);

        //  Pas de SMS en test
        // sms.sendSms(user.getPhone(), "OTP réinitialisation : " + otp);

        System.out.println(" [TEST] OTP reset pour " + user.getPhone() + " = " + otp);
        return otp;
    }


    public void confirmReset(ResetConfirmRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalide"));
        if (!user.getOtp().equals(req.getOtp()))
            throw new RuntimeException("OTP incorrect");
        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setOtp(null);
        userRepo.save(user);
    }
}

