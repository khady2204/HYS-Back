package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UpdateProfileRequest {
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String bio; // Facultatif
    private String profileImage; // base64 ou URL
    private List<Long> interetIds; // IDs des centres d'intérêt
}
