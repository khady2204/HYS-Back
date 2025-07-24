package HelpingYourSelf.com.HelpingYourSelf.DTO;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class DiscussionResponse {
    private UserSummary ami;
    private List<Message> messages;
}
