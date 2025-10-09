package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAddressResponse {
    private Long id;
    private String prenom;
    private String nom;
    private String adresse;
}
