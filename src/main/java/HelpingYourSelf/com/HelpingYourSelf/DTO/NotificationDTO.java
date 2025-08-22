package HelpingYourSelf.com.HelpingYourSelf.DTO;

import HelpingYourSelf.com.HelpingYourSelf.Entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String message;
    private boolean lue;
    private Instant dateEnvoi;
    private NotificationType type;
    private String emetteurNomComplet;
    private String cibleUrl;
    private String photoProfilEmetteur;



}
