package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentaireResponse {
    private Long id;
    private String contenu;
    private String auteurNom;
    private Long auteurId;
    private Instant createdAt;
    private int likesCount;
    private List<CommentaireResponse> reponses;
}
