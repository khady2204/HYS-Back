package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;

    private String email;
    private String phone;
    private String adresse;
    private String Bio;
    private Boolean isOnline;
    private String profileImage;
}
