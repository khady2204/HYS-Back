package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.NotificationDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Notification;
import HelpingYourSelf.com.HelpingYourSelf.Entity.NotificationType;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Envoie une notification en base de données et envoie en temps réel via WebSocket.
     */
    public Notification envoyerNotification(Notification notification) {
        notification.setDateEnvoi(Instant.now());
        Notification saved = notificationRepo.save(notification);

        // 🔔 Envoi en temps réel via WebSocket
        messagingTemplate.convertAndSend("/topic/notifications/" + notification.getDestinataire().getId(), mapToDTO(saved));
        return saved;
    }

    /**
     * Retourne toutes les notifications du destinataire (ordre décroissant).
     */
    public List<NotificationDTO> getNotifications(User user) {
        return notificationRepo.findByDestinataireOrderByDateEnvoiDesc(user)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Retourne les notifications non lues uniquement.
     */
    public List<NotificationDTO> getNotificationsNonLues(User user) {
        return notificationRepo.findByDestinataireOrderByDateEnvoiDesc(user)
                .stream()
                .filter(n -> !n.isLue())
                .map(this::mapToDTO)
                .toList();
    }

    /**
     * Marque une notification comme lue.
     */
    public void marquerCommeLue(Long id, User user) {
        Notification notification = notificationRepo.findById(id).orElseThrow();
        if (notification.getDestinataire().getId().equals(user.getId())) {
            notification.setLue(true);
            notificationRepo.save(notification);
        }
    }

    /**
     * Marque une notification comme lue et la retourne.
     */
    public NotificationDTO marquerEtLireNotification(Long id, User user) {
        Notification notification = notificationRepo.findById(id).orElseThrow();

        if (!notification.getDestinataire().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas lire cette notification.");
        }

        if (!notification.isLue()) {
            notification.setLue(true);
            notificationRepo.save(notification);
        }

        return mapToDTO(notification);
    }


    public Notification creerNotification(User emetteur, User destinataire, String message, NotificationType type, String cibleUrl) {
        Notification notification = Notification.builder()
                .emetteur(emetteur)
                .destinataire(destinataire)
                .message(message)
                .type(type)
                .cibleUrl(cibleUrl)
                .dateEnvoi(Instant.now())
                .build();
        return envoyerNotification(notification);
    }


    public NotificationDTO mapToDTO(Notification notification) {
        User emetteur = notification.getEmetteur();
        return new NotificationDTO(
                notification.getId(),
                notification.getMessage(),
                notification.isLue(),
                notification.getDateEnvoi(),
                notification.getType(),
                emetteur != null ? emetteur.getPrenom() + " " + emetteur.getNom() : "Système",
                emetteur != null ? emetteur.getProfileImage() : null,
                notification.getCibleUrl()
        );
    }
}
