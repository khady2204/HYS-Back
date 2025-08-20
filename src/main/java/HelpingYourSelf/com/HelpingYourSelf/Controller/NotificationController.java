package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.NotificationDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ Obtenir toutes les notifications
    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getMesNotifications(@AuthenticationPrincipal(expression = "user") User user) {
        List<NotificationDTO> notifications = notificationService.getNotifications(user);
        return ResponseEntity.ok(notifications);
    }

    // ✅ Obtenir uniquement les notifications non lues
    @GetMapping("/non-lues")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues(@AuthenticationPrincipal(expression = "user") User user) {
        List<NotificationDTO> notifications = notificationService.getNotificationsNonLues(user);
        return ResponseEntity.ok(notifications);
    }

    // ✅ Marquer une notification comme lue
    @PostMapping("/{id}/lue")
    public ResponseEntity<String> marquerLue(@PathVariable Long id, @AuthenticationPrincipal(expression = "user") User user) {
        notificationService.marquerCommeLue(id, user);
        return ResponseEntity.ok("Notification marquée comme lue.");
    }

    // ✅ Lire une notification (retourne le contenu + redirection possible via cibleUrl)
    @GetMapping("/{id}/lire")
    public ResponseEntity<NotificationDTO> lireNotification(@PathVariable Long id, @AuthenticationPrincipal(expression = "user") User user) {
        NotificationDTO dto = notificationService.marquerEtLireNotification(id, user);
        return ResponseEntity.ok(dto);
    }
}
