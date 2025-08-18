package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDTO {
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String ProfileImage;

}

