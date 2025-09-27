package HelpingYourSelf.com.HelpingYourSelf.Service;

import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.SnsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;

import java.util.Map;

@Service
public class AwsSnsService {

    @Value("${aws.sms.enabled:false}")
    private boolean awsSmsEnabled;

    @Value("${aws.region:af-south-1}") // Ajoutez cette ligne
    private String awsRegion;

    private final SnsClient snsClient;

    public AwsSnsService() {
        if (awsSmsEnabled) {
            // Initialise seulement si SMS est activé
            this.snsClient = SnsClient.builder()
                    .region(Region.of(awsRegion)) // Spécifie la région
                    .build();
        } else {
            this.snsClient = null;
            System.out.println("AWS SNS désactivé - mode simulation");
        }
    }

    public void sendSms(String to, String message) {
        if (!awsSmsEnabled || snsClient == null) {
            System.out.println("[AWS SMS SIMULÉ] à " + to + " : " + message);
            return;
        }

        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .phoneNumber(to)
                    .messageAttributes(Map.of(
                            "AWS.SNS.SMS.SMSType",
                            MessageAttributeValue.builder()
                                    .dataType("String")
                                    .stringValue("Transactional")
                                    .build()
                    ))
                    .build();

            snsClient.publish(request);
            System.out.println("[AWS SMS] envoyé à " + to);

        } catch (SnsException e) {
            System.err.println("Erreur AWS SNS: " + e.awsErrorDetails().errorMessage());
            // Fallback: log le message
            System.out.println("[AWS SMS FAIL] pour " + to + " : " + message);
        }
    }
}