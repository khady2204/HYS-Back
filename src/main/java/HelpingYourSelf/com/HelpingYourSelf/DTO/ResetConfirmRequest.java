package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class ResetConfirmRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;
}

