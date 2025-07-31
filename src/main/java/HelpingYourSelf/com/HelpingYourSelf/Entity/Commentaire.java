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
public class Commentaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenu;

    @ManyToOne
    private User auteur;

    @ManyToOne
    private Publication publication;

    @ManyToOne
    private Commentaire parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> reponses = new ArrayList<>();




    @ManyToMany
    private Set<User> likes = new HashSet<>();

    private Instant createdAt = Instant.now();
}

