package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.*;
import HelpingYourSelf.com.HelpingYourSelf.Entity.OtpRegistration;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.OtpRegistrationRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import HelpingYourSelf.com.HelpingYourSelf.Security.JwtTokenProvider;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final EmailService emailService;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwt;
    private final OtpRegistrationRepository otpRegistrationRepository;

    // ✅ Méthode modifié
    public void register(RegisterRequest req) {
        // ✅ Étape 1: Validation seulement
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas.");
        }

        if (userRepo.findByPhone(req.getPhone()).isPresent()) {
            throw new RuntimeException("Ce numéro est déjà utilisé.");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        // Nettoyer les anciennes inscriptions en cours
        otpRegistrationRepository.findByEmail(req.getEmail())
                .ifPresent(existing -> otpRegistrationRepository.delete(existing));

        // ✅ STOCKAGE TEMPORAIRE (pas de création User)
        String otpCode = String.valueOf(new Random().nextInt(899999) + 100000);

        OtpRegistration otpRegistration = OtpRegistration.builder()
                .email(req.getEmail())
                .phone(req.getPhone())
                .nom(req.getNom())
                .prenom(req.getPrenom())
                .adresse(req.getAdresse())
                .sexe(req.getSexe())
                .datenaissance(req.getDatenaissance())
                .password(encoder.encode(req.getPassword()))
                .otpCode(otpCode)
                .otpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES))
                .build();

        otpRegistrationRepository.save(otpRegistration);

        // Envoi OTP
        emailService.sendOtpEmail(req.getEmail(), otpCode, "l'activation de votre compte");
        System.out.println("[REGISTER] OTP envoyé (stockage temporaire) à " + req.getEmail() + " : " + otpCode);
    }

    public String sendOtp(OtpLoginRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email non trouvé"));

        if (user.getOtpExpiration() != null && user.getOtpExpiration().isAfter(Instant.now())) {
            Duration reste = Duration.between(Instant.now(), user.getOtpExpiration());
            if (reste.getSeconds() > 240) {
                throw new RuntimeException("Veuillez patienter avant de renvoyer un OTP.");
            }
        }

        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(code);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);
        String message = "Votre code de vérification HelpingYourSelf est : " + code;
        emailService.sendOtpEmail(user.getEmail(), code, "la connexion à votre compte");

        System.out.println("[RESEND] Nouveau code OTP envoyé à : " + user.getEmail());
        return code;
    }

    public String verifyOtp(OtpVerifyRequest req, String ip) {
        // ✅ CORRECTION : Chercher dans otp_registration au lieu de user
        OtpRegistration otpRegistration = otpRegistrationRepository
                .findByEmailAndOtpCode(req.getEmail(), req.getOtp())
                .orElseThrow(() -> new RuntimeException("OTP invalide ou expiré"));

        if (otpRegistration.getOtpExpiration().isBefore(Instant.now())) {
            otpRegistrationRepository.delete(otpRegistration);
            throw new RuntimeException("OTP expiré");
        }

        // ✅ CRÉATION de l'User APRÈS OTP vérifié
        User user = User.builder()
                .nom(otpRegistration.getNom())
                .prenom(otpRegistration.getPrenom())
                .adresse(otpRegistration.getAdresse())
                .phone(otpRegistration.getPhone())
                .email(otpRegistration.getEmail())
                .sexe(otpRegistration.getSexe())
                .datenaissance(otpRegistration.getDatenaissance())
                .password(otpRegistration.getPassword()) // Déjà encodé
                .enabled(true) // ✅ Activé immédiatement
                .roles(Set.of(Role.USER))
                .build();

        userRepo.save(user);

        // ✅ Nettoyer l'OTP temporaire
        otpRegistrationRepository.delete(otpRegistration);

        System.out.println("[REGISTER] Utilisateur créé après OTP vérifié: " + req.getEmail());

        return jwt.generateToken(user);
    }
    public String sendResetOtp(ResetRequest req) {
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Email introuvable"));

        String otp = String.valueOf(new Random().nextInt(899999) + 100000);
        user.setOtp(otp);
        user.setOtpExpiration(Instant.now().plus(5, ChronoUnit.MINUTES));
        userRepo.save(user);
        String message = "Votre code de réinitialisation HelpingYourSelf est : " + otp;
        emailService.sendOtpEmail(user.getEmail(), otp, "la réinitialisation de votre mot de passe");

        System.out.println("[RESET] OTP envoyé à : " + user.getEmail());
        return otp;
    }

    public void confirmReset(ResetConfirmRequest req) {
        try {
            System.out.println("🔧 confirmReset called for: " + req.getEmail());

            // ✅ CORRECTION 1: Chercher par EMAIL
            User user = userRepo.findByEmail(req.getEmail())
                    .orElseThrow(() -> {
                        System.out.println("❌ User not found for email: " + req.getEmail());
                        return new RuntimeException("Utilisateur introuvable");
                    });

            System.out.println("✅ User found: " + user.getId());

            // ✅ CORRECTION 2: Vérifier que l'OTP a été validé
            // Soit via isOtpVerified, soit en vérifiant que l'OTP est null (utilisé)
            if (user.getOtp() != null) {
                System.out.println("❌ OTP not validated yet: " + user.getOtp());
                throw new RuntimeException("OTP non validé. Veuillez d'abord vérifier le code.");
            }

            // ✅ CORRECTION 3: Vérifier les mots de passe
            if (!req.getNewPassword().equals(req.getConfirmPassword())) {
                System.out.println("❌ Passwords don't match");
                throw new RuntimeException("Les mots de passe ne correspondent pas");
            }

            // ✅ Mettre à jour le mot de passe
            user.setPassword(encoder.encode(req.getNewPassword()));
            user.setOtp(null);
            user.setOtpExpiration(null);
            user.setIsOtpVerified(false); // Remettre à false
            userRepo.save(user);

            System.out.println("✅ Password reset successful for user: " + user.getId());

        } catch (Exception e) {
            System.out.println("❌ Error in confirmReset: " + e.getMessage());
            throw e;
        }
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
        user.setIsOnline(true);
        user.setLastOnlineAt(null);
        userRepo.save(user);

        return jwt.generateToken(user);
    }


    public void verifyResetOtp(VerifyOtpRequest req) {
        // CHANGEMENT : Recherche par EMAIL au lieu de téléphone
        User user = userRepo.findByEmail(req.getEmail()) // ← Modifié ici
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (user.getOtpLockUntil() != null && user.getOtpLockUntil().isAfter(Instant.now())) {
            throw new RuntimeException("Trop de tentatives échouées. Réessayez après " + user.getOtpLockUntil());
        }

        if (user.getOtp() == null || !user.getOtp().equals(req.getOtp())) {
            int maxAttempts = 5;
            user.setOtpAttempts(user.getOtpAttempts() + 1);

            if (user.getOtpAttempts() >= maxAttempts) {
                user.setOtpLockUntil(Instant.now().plusSeconds(15 * 60));
                user.setOtpAttempts(0);
            }

            userRepo.save(user);
            throw new RuntimeException("OTP incorrect");
        }

        if (user.getOtpExpiration() == null || user.getOtpExpiration().isBefore(Instant.now())) {
            throw new RuntimeException("OTP expiré");
        }

        user.setOtp(null);
        user.setOtpExpiration(null);
        user.setIsOtpVerified(true);
        user.setOtpAttempts(0);
        user.setOtpLockUntil(null);
        userRepo.save(user);

        System.out.println("✅ OTP reset validated and cleared for: " + req.getEmail());
    }

    public void createSuperAdmin(RegisterRequest req) {
        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas.");
        }

        if (userRepo.findByPhone(req.getPhone()).isPresent()) {
            throw new RuntimeException("Ce numéro est déjà utilisé.");
        }

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            throw new RuntimeException("Cet email est déjà utilisé.");
        }

        User user = new User();
        user.setNom(req.getNom());
        user.setPrenom(req.getPrenom());
        user.setAdresse(req.getAdresse());
        user.setPhone(req.getPhone());
        user.setEmail(req.getEmail());
        user.setSexe(req.getSexe());
        user.setDatenaissance(req.getDatenaissance());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setEnabled(true);
        user.setRoles(Set.of(Role.SUPERADMIN));

        userRepo.save(user);
    }

    public String processGoogleToken(String idTokenString) {
        GoogleIdTokenVerifier verifier;
        try {
            verifier = new GoogleIdTokenVerifier.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    new GsonFactory() // Utilisez GsonFactory au lieu de JacksonFactory
            )
                    .setAudience(Collections.singletonList("440755805165-0lsmqghacaffjq393md8g6bpngn1d3a6.apps.googleusercontent.com"))
                    .build();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la vérification Google", e);
        }

        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException("La vérification du token Google a échoué", e);
        }

        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            String email = payload.getEmail();
            String name = (String) payload.get("name");

            Optional<User> optionalUser = userRepo.findByEmail(email);
            User user = optionalUser.orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setNom(name);
                newUser.setEnabled(true);
                newUser.setRoles(Set.of(Role.USER));
                return newUser;
            });

            // S'assurer que l'utilisateur dispose d'un identifiant avant de générer le token JWT
            if (user.getId() == null) {
                user = userRepo.save(user);
            }

            String token = jwt.generateTokenFromUser(user);
            user.setToken(token);
            userRepo.save(user);

            return token;
        } else {
            throw new RuntimeException("Token Google invalide");
        }
    }

    public User getCurrentUser(HttpServletRequest request) {
        String token = jwt.resolveToken(request);
        String subject = jwt.getSubjectFromToken(token);

        if (subject == null) {
            throw new RuntimeException("Token invalide");
        }

        Optional<User> user = userRepo.findByPhone(subject);

        if (user.isEmpty()) {
            try {
                Long userId = Long.parseLong(subject);
                user = userRepo.findById(userId);
            } catch (NumberFormatException ignored) {
                // Le subject n'est pas un identifiant numérique
            }
        }

        if (user.isEmpty()) {
            user = userRepo.findByEmail(subject);
        }

        return user.orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }




}
