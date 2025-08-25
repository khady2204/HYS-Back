package HelpingYourSelf.com.HelpingYourSelf.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiscussionResponse {
    private UserSummary ami;
    private List<MessageResponse> messages;
}
