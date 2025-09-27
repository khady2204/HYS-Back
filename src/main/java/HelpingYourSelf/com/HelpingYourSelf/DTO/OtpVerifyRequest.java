package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String Email;
    private String otp;
    private String deviceInfo;
}
