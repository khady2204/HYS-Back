package HelpingYourSelf.com.HelpingYourSelf.DTO;

import java.time.Instant;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Instant timestamp;
    private String mediaUrl;
    private String mediaType;
    private Integer audioDuration;

    public MessageResponse(Long id, Long senderId, Long receiverId, String content, Instant timestamp,
                           String mediaUrl, String mediaType, Integer audioDuration) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
        this.mediaUrl = mediaUrl;
        this.mediaType = mediaType;
        this.audioDuration = audioDuration;
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
}
