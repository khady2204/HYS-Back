package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String Email;
    private String otp;

}
