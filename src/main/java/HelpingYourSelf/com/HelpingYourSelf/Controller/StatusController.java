package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.StatusDTO;
import HelpingYourSelf.com.HelpingYourSelf.Security.SecurityUtils;
import HelpingYourSelf.com.HelpingYourSelf.Service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
@Tag(name = "Status", description = "API pour la gestion des statuts éphémères")
public class StatusController {

    private final StatusService statusService;

/*    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createStatus(
            @RequestParam("userId") Long userId,
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "files", required = false) MultipartFile[] files) {

        try {
            StatusDTO status = statusService.createStatus(userId, text, files != null ? files : new MultipartFile[0]);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la création du statut: " + e.getMessage()));
        }
    }*/
@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public ResponseEntity<?> createStatus(
        @RequestParam(value = "text", required = false) String text,
        @RequestParam(value = "files", required = false) MultipartFile[] files) {

    try {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        StatusDTO status = statusService.createStatus(currentUserId, text, files != null ? files : new MultipartFile[0]);
        return ResponseEntity.ok(status);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de la création du statut: " + e.getMessage()));
    }
}

    @GetMapping("/feed/{userId}")
    @Operation(summary = "Obtenir le fil d'actualité des statuts",
               description = "Récupère les statuts des utilisateurs avec qui l'utilisateur a une conversation")
    @ApiResponse(responseCode = "200", description = "Liste des statuts récupérée avec succès",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatusDTO.class))))
    public ResponseEntity<List<StatusDTO>> getStatusFeed(@PathVariable Long userId) {
        List<StatusDTO> statuses = statusService.getStatusesForUser(userId);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Obtenir les statuts d'un utilisateur",
               description = "Récupère tous les statuts actifs d'un utilisateur spécifique")
    @ApiResponse(responseCode = "200", description = "Statuts de l'utilisateur récupérés avec succès",
                content = @Content(array = @ArraySchema(schema = @Schema(implementation = StatusDTO.class))))
    public ResponseEntity<List<StatusDTO>> getUserStatuses(@PathVariable Long userId) {
        List<StatusDTO> statuses = statusService.getUserStatuses(userId);
        return ResponseEntity.ok(statuses);
    }
}
