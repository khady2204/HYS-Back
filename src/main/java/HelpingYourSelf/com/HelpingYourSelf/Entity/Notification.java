package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String message;

    private boolean lue = false;

    private Instant dateEnvoi = Instant.now();

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @ManyToOne
    private User emetteur;

    @ManyToOne
    private User destinataire;

    @Column(length = 255)
    private String cibleUrl;






}
