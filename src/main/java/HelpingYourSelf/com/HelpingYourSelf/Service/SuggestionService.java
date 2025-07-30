package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.SuggestionResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final UserRepository userRepository;

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

            SuggestionResponse s = new SuggestionResponse();
            s.setNom(autre.getNom());
            s.setPrenom(autre.getPrenom());
            s.setCompatibilite(compatibilite);
            // s.setInteretsCommuns("Intérêts communs : " + nbCommuns);
            if (nbCommuns > 0) {
                String nomsInterets = String.join(", ", communs.stream().map(Interet::getNom).toList());
                s.setInteretsCommuns("Intérêts communs : " + nomsInterets);
            } else {
                s.setInteretsCommuns("Intérêts communs : aucun");
            }

            // ❗ Personnalise ici avec autre.getPhotoUrl() si c’est disponible
            s.setPhotoUrl("https://example.com/avatar.jpg");

            suggestions.add(s);
        }

        return suggestions;
    }
}
