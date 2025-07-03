package HelpingYourSelf.com.HelpingYourSelf.Service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    @Value("${twilio.enabled:false}")
    private boolean twilioEnabled;

    @Value("${twilio.account.sid}")
    private String sid;

    @Value("${twilio.auth.token}")
    private String token;

    @Value("${twilio.phone.number}")
    private String from;

    @PostConstruct
    public void init() {
        if (twilioEnabled) {
            Twilio.init(sid, token);
        }
    }

    public void sendSms(String to, String message) {
        if (twilioEnabled) {
            Message.creator(new PhoneNumber(to), new PhoneNumber(from), message).create();
        } else {
            System.out.println("[DEV-MODE] SMS simulé à " + to + " : " + message);
        }
    }
}



