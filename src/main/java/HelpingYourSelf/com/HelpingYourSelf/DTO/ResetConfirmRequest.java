package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class ResetConfirmRequest {
    private String phone;
    private String newPassword;
    private String confirmPassword;
}

