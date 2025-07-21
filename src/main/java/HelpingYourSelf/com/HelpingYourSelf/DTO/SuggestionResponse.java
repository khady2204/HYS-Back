package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class SuggestionResponse {
    private String nom;
    private String prenom;
    private String photoUrl;
    private int compatibilite;
    private String interetsCommuns;
}
