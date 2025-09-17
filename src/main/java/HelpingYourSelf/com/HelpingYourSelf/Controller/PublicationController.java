package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicationDTO;
import HelpingYourSelf.com.HelpingYourSelf.Service.PublicationService;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/publications")
@RequiredArgsConstructor
public class PublicationController {
    private final PublicationService publicationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PublicationDTO> poster(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam(required = false) String texte,
            @RequestParam(required = false) List<MultipartFile> fichiers,
            @RequestParam(required = false) List<String> descriptions) {

        return ResponseEntity.ok(publicationService.poster(user, texte, fichiers, descriptions));
    }

    @GetMapping
    public ResponseEntity<Page<PublicationDTO>> getPublications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(publicationService.getPublications(
                PageRequest.of(page, size, Sort.by("createdAt").descending())));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<String> like(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(publicationService.toggleLike(user, id));
    }

    @PostMapping("/{id}/partager")
    public ResponseEntity<String> partager(
            @AuthenticationPrincipal(expression = "user") User user,
            @PathVariable Long id) {
        return ResponseEntity.ok(publicationService.incrementerPartage(id, user));
    }

    @GetMapping("/{id}/commentaires")
    public ResponseEntity<?> getCommentaires(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.getCommentaires(id));
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<String> deletePub(
            @PathVariable Long id,
            @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(publicationService.supprimerPublication(id, user));
    }

    @PutMapping("/{id}/update-texte")
    public ResponseEntity<String> modifierTextePublication(
            @PathVariable Long id,
            @RequestParam String nouveauTexte,
            @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(publicationService.updateTexte(id, nouveauTexte, user));
    }
}