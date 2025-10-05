package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String texte;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auteur_id", nullable = false)
    private User auteur;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Media> medias = new ArrayList<>();

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Commentaire> commentaires = new ArrayList<>();

    @Column(name = "nombre_likes", nullable = false)
    private int nombreLikes = 0;
    
    @Column(name = "nombre_partages", nullable = false)
    private int nombrePartages = 0;

    @Column(updatable = false)
    private Instant createdAt = Instant.now();

    // Méthodes utilitaires
    public void addMedia(Media media) {
        medias.add(media);
        media.setPublication(this);
    }

    public void removeMedia(Media media) {
        medias.remove(media);
        media.setPublication(null);
    }
}