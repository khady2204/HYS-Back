package HelpingYourSelf.com.HelpingYourSelf.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicUserDTO {
    private String prenom;
    private String nom;
    private String email;
    private String adresse;
    private String profileImage;
    @JsonProperty("online")
    private Boolean isOnline;


    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant lastOnlineAt;     // ISO-8601 (brut, utile pour les applis)

    private String lastOnlineLabel;   // “Aujourd’hui 14:32”, “Hier 08:10”, “12/08/2025 à 09:05”


}

