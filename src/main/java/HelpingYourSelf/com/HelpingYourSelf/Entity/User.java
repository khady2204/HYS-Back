package HelpingYourSelf.com.HelpingYourSelf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.*;

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
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @Builder.Default
    private boolean enabled = true;

    @Builder.Default
    private boolean blocked = false;

    private int otpAttempts = 0;
    private Instant otpLockUntil;


    private String otp;
    private Instant otpExpiration;
    private Boolean isOtpVerified = false;


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

    // ✅ Relation ManyToMany avec les centres d'intérêt
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_interet",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "interet_id")
    )
    @Builder.Default
    private List<Interet> interets = new ArrayList<>();
}
