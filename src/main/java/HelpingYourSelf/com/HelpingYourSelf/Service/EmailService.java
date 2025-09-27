package HelpingYourSelf.com.HelpingYourSelf.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);

            // ✅ FORCER l'expéditeur même sans alias confirmé
            helper.setFrom("no-reply@hysinternational.com", "HYS International");
            helper.setTo(toEmail);
            helper.setSubject("Votre code de vérification - HYS International");

            String emailContent = """
            Cher utilisateur,
            
            Votre code de vérification pour %s est : %s
            
            Ce code expirera dans 10 minutes.
            
            Si vous n'avez pas demandé ce code, veuillez ignorer cet email.
            
            Cordialement,
            L'équipe HYS International
            """.formatted(purpose, otpCode);

            helper.setText(emailContent, false);

            mailSender.send(message);
            System.out.println("✅ OTP envoyé à : " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Erreur envoi email: " + e.getMessage());
            throw new RuntimeException("Erreur d'envoi d'email OTP");
        }
    }
}