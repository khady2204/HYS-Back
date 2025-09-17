package HelpingYourSelf.com.HelpingYourSelf.Entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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

    private int otpAttempts = 0;
    private Instant otpLockUntil;


    private String otp;
    private Instant otpExpiration;
    private Boolean isOtpVerified = false;



    private String deviceInfo;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(name = "last_online_at")
    private Instant lastOnlineAt;

    // dernière activité vue par le backend (toute requête avec JWT valide)
    @Column(name = "last_activity_at")
    private Instant lastActivityAt;


    @Column(length = 500)
    private String token;


    public String getUsername() {
        return email != null ? email : phone;
    }

    // Bio facultative
    @Column(length = 500)
    private String bio;

    // URL ou base64 de l'image de profil
    private String profileImage;

    @ManyToMany
    @JoinTable(
            name = "user_interet",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "interet_id")
    )
     private List<Interet> interets;

    @ManyToOne
    @JoinColumn(name = "gestionnaire_id")
    @JsonIgnore
    private User gestionnaire;

    @ManyToMany
    @JoinTable(
        name = "abonnements",
        joinColumns = @JoinColumn(name = "suiveur_id"),
        inverseJoinColumns = @JoinColumn(name = "suivi_id")
    )
    @JsonIgnore
    private Set<User> abonnements = new HashSet<>();

    @ManyToMany(mappedBy = "abonnements")
    @JsonIgnore
    private Set<User> abonnes = new HashSet<>();




    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by_id")
    @JsonIgnore
    private User createdBy;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
    }

    public String getProfileImage() {
        return profileImage != null ? "http://localhost:8081/uploads/" + profileImage : null;
    }

}
