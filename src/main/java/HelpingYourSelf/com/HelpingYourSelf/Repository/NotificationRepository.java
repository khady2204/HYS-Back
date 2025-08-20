package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Notification;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByDestinataireOrderByDateEnvoiDesc(User destinataire);
}
