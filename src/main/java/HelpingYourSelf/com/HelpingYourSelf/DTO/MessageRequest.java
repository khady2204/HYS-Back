package HelpingYourSelf.com.HelpingYourSelf.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public class MessageRequest {

    @NotNull
    private Long receiverId;

    private String content;

    private MultipartFile mediaFile;

    private String mediaType; // e.g., "image", "video"

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MultipartFile getMediaFile() {
        return mediaFile;
    }

    public void setMediaFile(MultipartFile mediaFile) {
        this.mediaFile = mediaFile;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
