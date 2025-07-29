package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@AllArgsConstructor
public class CommentaireDTO {
    private Long id;
    private String contenu;
    private String auteur;
    private Instant createdAt;
    private int nombreLikes;
    private List<CommentaireDTO> reponses;
}

