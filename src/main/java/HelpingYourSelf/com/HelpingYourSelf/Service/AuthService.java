package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas.");
        }

        if (userRepo.findByPhone(req.getPhone()).isPresent()) {
            throw new RuntimeException("Ce numéro est déjà utilisé.");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        User u = new User();
        u.setNom(req.getNom());
        u.setPrenom(req.getPrenom());
        u.setAdresse(req.getAdresse());
        u.setPhone(req.getPhone());
        u.setEmail(req.getEmail());
        u.setSexe(req.getSexe());
        u.setDatenaissance(req.getDatenaissance());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setEnabled(false); // Activer uniquement après vérification OTP
        u.setRoles(Set.of(Role.USER));

        // Générer et enregistrer l’OTP
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        u.setOtp(code);
        u.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));

        userRepo.save(u);

        // Envoyer l’OTP via console (à remplacer par SMS Twilio en prod)
        System.out.println("[REGISTER] OTP envoyé au numéro " + req.getPhone() + " : " + code);
    }



    public String sendOtp(OtpLoginRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro non trouvé"));

        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(code);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);

        System.out.println("[TEST] Code OTP : " + code);
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
        user.setEnabled(true);

        userRepo.save(user);


        return jwt.generateToken(user);
    }


    public String sendResetOtp(ResetRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Numéro introuvable"));

        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);

        System.out.println("[TEST] OTP reset : " + otp);
        return otp;
    }

    public void confirmReset(ResetConfirmRequest req) {
        User user = userRepo.findByPhone(req.getPhone())
                .orElseThrow(() -> new RuntimeException("Invalide"));
        if (!user.getOtp().equals(req.getOtp()))
            throw new RuntimeException("OTP incorrect");
        user.setPassword(encoder.encode(req.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiration(null);
        userRepo.save(user);
    }

    public Optional<User> loginWithEmail(String email, String password) {
        return userRepo.findByEmail(email)
                .filter(u -> encoder.matches(password, u.getPassword()));
    }

    public Optional<User> loginWithPhone(String phone, String password) {
        return userRepo.findByPhone(phone)
                .filter(u -> encoder.matches(password, u.getPassword()));
    }

    public String login(LoginRequest req, String ip) {
        User user;

        if (req.getPhone() != null && !req.getPhone().isEmpty()) {
            user = userRepo.findByPhone(req.getPhone())
                    .orElseThrow(() -> new RuntimeException("Numéro introuvable"));
        } else if (req.getEmail() != null && !req.getEmail().isEmpty()) {
            user = userRepo.findByEmail(req.getEmail())
                    .orElseThrow(() -> new RuntimeException("Email introuvable"));
        } else {
            throw new RuntimeException("Email ou téléphone requis");
        }

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        if (!user.isEnabled()) {
            throw new RuntimeException("Compte non activé.");
        }

        user.setLastLoginIp(ip);
        userRepo.save(user);

        return jwt.generateToken(user);
    }


}
