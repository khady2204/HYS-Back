package HelpingYourSelf.com.HelpingYourSelf.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Lightweight representation of a {@link HelpingYourSelf.com.HelpingYourSelf.Entity.Message}
 * returned to clients.  The explicit {@link JsonProperty} annotations guarantee
 * that identifier fields are always serialised, allowing consumers to
 * easily determine which user sent or received a particular message.
 */
public class MessageResponse {

    private Long id;

    @JsonProperty("senderId")
    private Long senderId;

    @JsonProperty("receiverId")
    private Long receiverId;

    private String content;
    private Instant timestamp;
    private String mediaUrl;
    private String mediaType;
    private Integer audioDuration;
    private boolean read;

    public MessageResponse(Long id, Long senderId, Long receiverId, String content, Instant timestamp,
                           String mediaUrl, String mediaType, Integer audioDuration, boolean read) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.audioDuration = audioDuration;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Integer getAudioDuration() {
        return audioDuration;
    }

    public boolean isRead() {
        return read;
    }
}
