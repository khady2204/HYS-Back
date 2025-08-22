package HelpingYourSelf.com.HelpingYourSelf.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(length = 1000)
    private String text;
    
    @ElementCollection
    @CollectionTable(name = "status_media", joinColumns = @JoinColumn(name = "status_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls = new ArrayList<>();
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private Instant expiresAt;
    
    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.expiresAt = this.createdAt.plusSeconds(24 * 60 * 60); // 24 heures
    }
    
    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }
}
