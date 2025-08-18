package HelpingYourSelf.com.HelpingYourSelf.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.Instant;

/**
 * Entity representing a message exchanged between two users.
 *
 * <p>
 * The {@code isRead} field is mapped to the {@code is_read} column in the database. It defaults
 * to {@code false} and is ensured to be {@code false} at persistence time if not explicitly set.
 * </p>
 */

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @JsonIgnore
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @JsonIgnore
    private User receiver;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false)
    private Instant timestamp;

    @Column(name = "media_url", length = 255)
    private String mediaUrl;

    @Column(name = "media_type", length = 50)
    private String mediaType;

    @Column(name = "audio_duration")
    private Integer audioDuration;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @PrePersist
    protected void prePersist() {
        // Ensure the read flag is always initialized
        this.isRead = false;
    }

    public Message() {
        this.timestamp = Instant.now();
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Integer getAudioDuration() {
        return audioDuration;
    }

    public void setAudioDuration(Integer audioDuration) {
        this.audioDuration = audioDuration;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        this.isRead = read;
    }
}
