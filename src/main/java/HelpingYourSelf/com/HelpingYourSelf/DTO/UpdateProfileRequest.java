package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {
    private String prenom;
    private String nom;
    private String email;
    private String phone;
    private String adresse;
    private String bio;
    private String profileImage;
    private List<Long> interetIds;
}
