package HelpingYourSelf.com.HelpingYourSelf.DTO;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaDTO {
    private String url;
    private String type;
    private String description;
}