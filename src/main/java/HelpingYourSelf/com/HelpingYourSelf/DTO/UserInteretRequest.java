package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserInteretRequest {
    private Long userId;
    private List<Long> interetIds;
}
