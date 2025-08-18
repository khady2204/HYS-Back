package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewPasswordRequest {
    private String newPassword;
    private String confirmPassword;
}
