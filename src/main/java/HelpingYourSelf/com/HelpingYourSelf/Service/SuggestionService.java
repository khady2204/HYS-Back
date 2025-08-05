package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.SuggestionResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Notification;
import HelpingYourSelf.com.HelpingYourSelf.Entity.NotificationType;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    public List<SuggestionResponse> getSuggestionsFor(User currentUser) {
        List<Interet> mesInterets = currentUser.getInterets();
        if (mesInterets == null || mesInterets.isEmpty()) {
            return new ArrayList<>();
        }

        List<User> autres = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId()))
                .toList();

        List<SuggestionResponse> suggestions = new ArrayList<>();

        for (User autre : autres) {
            List<Interet> interetsAutre = autre.getInterets();
            if (interetsAutre == null || interetsAutre.isEmpty()) continue;

            List<Interet> communs = interetsAutre.stream()
                    .filter(mesInterets::contains)
                    .toList();

            int nbCommuns = communs.size();
            int total = mesInterets.size() + interetsAutre.size();
            int compatibilite = total == 0 ? 0 : (int) ((2.0 * nbCommuns / total) * 100);

            //  Notification si compatibilité >= 50 %
            if (compatibilite >= 50) {
                notificationService.envoyerNotification(Notification.builder()
                        .emetteur(autre)
                        .destinataire(currentUser)
                        .message("Nouveau profil suggéré : " + autre.getPrenom() + " (" + compatibilite + "%)")
                        .type(NotificationType.SUGGESTION)
                        .build());
            }

            SuggestionResponse s = new SuggestionResponse();
            s.setNom(autre.getNom());
            s.setPrenom(autre.getPrenom());
            s.setCompatibilite(compatibilite);
            s.setInteretsCommuns("Intérêts communs : " + nbCommuns);
            s.setPhotoUrl("https://example.com/avatar.jpg"); // à remplacer par la vraie URL
            suggestions.add(s);
        }

        return suggestions;
    }

}
