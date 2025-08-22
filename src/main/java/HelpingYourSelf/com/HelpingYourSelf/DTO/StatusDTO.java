package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private String userProfileImage;
    private String text;
    private List<String> mediaUrls;
    private Instant createdAt;
    private Instant expiresAt;
    private long timeLeftInHours;
}
