package HelpingYourSelf.com.HelpingYourSelf.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otpCode, String purpose) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("segnanelaye@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Votre code de vérification - HelpingYourSelf");

        String emailContent = """
        Cher utilisateur,
        
        Votre code de vérification pour %s est : %s
        
        Ce code expirera dans 10 minutes.
        
        Si vous n'avez pas demandé ce code, veuillez ignorer cet email.
        
        Cordialement,
        L'équipe HelpingYourSelf
        """.formatted(purpose, otpCode);

        message.setText(emailContent);

        mailSender.send(message);
    }

}
