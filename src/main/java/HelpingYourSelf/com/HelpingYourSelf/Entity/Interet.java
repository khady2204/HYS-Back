package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Interet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom; // Exemple : "Musique", "Sport", "Lecture"

    // (Facultatif) Pour une relation bidirectionnelle :
    @ManyToMany(mappedBy = "interets")
    @ToString.Exclude
    private List<User> users;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Interet)) return false;
        Interet interet = (Interet) o;
        return Objects.equals(id, interet.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

