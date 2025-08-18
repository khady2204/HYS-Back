package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class VerifyOtpRequest {
    private String phone;
    private String otp;
}
