package HelpingYourSelf.com.HelpingYourSelf.DTO;

import java.time.Instant;

public class MessageResponse {

    private Long id;
    private Long senderId;
    private Long receiverId;
    private String content;
    private Instant timestamp;

    public MessageResponse(Long id, Long senderId, Long receiverId, String content, Instant timestamp) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = timestamp;
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
}
