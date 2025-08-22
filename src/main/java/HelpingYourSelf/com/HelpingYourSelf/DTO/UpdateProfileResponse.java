package HelpingYourSelf.com.HelpingYourSelf.DTO;

import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateProfileResponse {
    private String token;
    private UserSummary user;
}
