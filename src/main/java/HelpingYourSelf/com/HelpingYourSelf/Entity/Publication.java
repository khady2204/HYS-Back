package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;


import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String texte;

    private String mediaUrl;
    private String mediaType;

    @ManyToOne
    private User auteur;

    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();

    @ManyToMany
    private Set<User> likes = new HashSet<>();

    private int nombrePartages;
}


