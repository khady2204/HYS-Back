package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Media {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;
    private String type; // "image" ou "video"
    private String description;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;
}