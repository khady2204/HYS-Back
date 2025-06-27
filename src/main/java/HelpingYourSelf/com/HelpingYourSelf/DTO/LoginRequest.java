package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String phone;
    private String email;
    private String password;
}
