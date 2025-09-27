package HelpingYourSelf.com.HelpingYourSelf.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsGatewayService {

    private final AwsSnsService awsSnsService;
    private final EmailService emailService; // Ajouter EmailService

    @Value("${sms.provider:none}") // 'aws', 'email', ou 'none'
    private String smsProvider;

    public void sendSms(String to, String message) {
        if ("aws".equalsIgnoreCase(smsProvider)) {
            awsSnsService.sendSms(to, message);
        } else if ("email".equalsIgnoreCase(smsProvider)) {
            // Fallback: envoyer par email avec le numéro comme destinataire
            emailService.sendOtpEmail("admin@hysinternational.com",
                    "SMS Fallback: " + message, "Notification système");
        } else {
            System.out.println("[SMS SIMULÉ] à " + to + " : " + message);
        }
    }
}



