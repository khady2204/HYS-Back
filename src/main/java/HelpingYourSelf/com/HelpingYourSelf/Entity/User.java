package HelpingYourSelf.com.HelpingYourSelf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nom;
    private String prenom;
    private String email;
    private String phone;

    private String sexe;

    @JsonIgnore
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private boolean enabled = true;
    private boolean blocked = false;

    private String otp;

    private Instant otpExpiration;

    private String lastLoginIp;
    private String deviceInfo;

    @ManyToOne
    @JoinColumn(name = "gestionnaire_id")
    private User gestionnaire;

    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();

    }

}
