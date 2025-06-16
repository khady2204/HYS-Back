package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    private String nom;
    private String phone;
    private String email;
    private String password;
    private Set<String> roles;
}
