package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

import java.util.Date;

@Data
public class RegisterRequest {
    private String nom;
    private String prenom;
    private String adresse;
    private String email;
    private String phone;
    private String sexe;
    private Date datenaissance;
    private String password;
    private String confirmPassword;
}
