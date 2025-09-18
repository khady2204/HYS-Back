package HelpingYourSelf.com.HelpingYourSelf.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SmsGatewayService {


    private final AwsSnsService awsSnsService;

    @Value("${sms.provider:aws}") // 'twilio' ou 'aws'
    private String smsProvider;

    public void sendSms(String to, String message) {
        if ("aws".equalsIgnoreCase(smsProvider)) {
            awsSnsService.sendSms(to, message);
        }
    }
}



