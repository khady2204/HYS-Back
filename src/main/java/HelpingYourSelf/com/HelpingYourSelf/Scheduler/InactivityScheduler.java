package HelpingYourSelf.com.HelpingYourSelf.Scheduler;

import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InactivityScheduler {

    private final UserRepository userRepository;

    // Durée d'inactivité tolérée avant de passer offline
    private static final Duration TIMEOUT = Duration.ofMinutes(15);

    /**
     * Tâche planifiée qui s'exécute toutes les 60s.
     * Elle passe "offline" les utilisateurs dont la dernière activité
     * (lastActivityAt) est antérieure à maintenant - TIMEOUT.
     */
    @Scheduled(fixedDelay = 60_000)
    public void markInactiveUsersOffline() {
        Instant limit = Instant.now().minus(TIMEOUT);

        // Variante efficace: requête ciblée (voir méthode dans UserRepository ci-dessous)
        List<User> toOff = userRepository.findByIsOnlineTrueAndLastActivityAtBefore(limit);

        if (toOff.isEmpty()) {
            return;
        }

        for (User u : toOff) {
            u.setIsOnline(false);
            // On fixe la dernière connexion au moment de la dernière activité connue (ou maintenant si null)
            u.setLastOnlineAt(u.getLastActivityAt() != null ? u.getLastActivityAt() : Instant.now());
        }
        userRepository.saveAll(toOff);
        log.info("InactivityScheduler: {} utilisateur(s) passé(s) offline.", toOff.size());
    }
}
