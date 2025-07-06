package HelpingYourSelf.com.HelpingYourSelf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;
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
    private String adresse;
    @Column(unique = true, nullable = false)
    private String email;



    private String phone;
    private String sexe;
    private Date datenaissance;

    @JsonIgnore
    private String password;

    @Transient
    @JsonIgnore
    private String confirmPassword;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private boolean enabled = true;
    private boolean blocked = false;

    private String otp;
    private Instant otpExpiration;

    private String lastLoginIp;
    private String deviceInfo;


    public String getUsername() {
        return email != null ? email : phone;
    }


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
