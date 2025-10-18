package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;

@Entity
@Table(name = "otp_registration")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private String adresse;
    private String sexe;

    @Temporal(TemporalType.DATE)
    private Date datenaissance;

    @Column(nullable = false)
    private String password; // Déjà encodé

    @Column(nullable = false, length = 6)
    private String otpCode;

    @Column(nullable = false)
    private Instant otpExpiration;

    private Instant createdAt = Instant.now();

    private Instant lastOtpSent; // Pour éviter le spam

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }
}