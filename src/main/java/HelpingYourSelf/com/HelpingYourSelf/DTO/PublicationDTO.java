package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class PublicationDTO {
    private Long id;
    private String texte;
    private String mediaUrl;
    private String mediaType;
    private String auteurNom;
    private Instant createdAt;
    private int nombreCommentaires;
    private int nombreLikes;
    private int nombrePartages;
}

