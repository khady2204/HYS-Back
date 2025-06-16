package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Entity
@Data
public class User {

    @Id @GeneratedValue
    private Long id;

    private String nom;
    private String phone;
    private String email;
    private String password;
    private boolean enabled;

    private String otp;
    private Instant otpExpiration;

    private String lastLoginIp;
    private String deviceInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> roles;

    
}
