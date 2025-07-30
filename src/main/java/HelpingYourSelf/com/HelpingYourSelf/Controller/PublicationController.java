package HelpingYourSelf.com.HelpingYourSelf.Controller;

import HelpingYourSelf.com.HelpingYourSelf.Service.PublicationService;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/publications")
public class PublicationController {

    @Autowired
    private PublicationService publicationService;

    @PostMapping("/poster")
    public ResponseEntity<?> poster(
            @AuthenticationPrincipal(expression = "user") User user,
            @RequestParam String texte,
            @RequestParam(required = false) MultipartFile media) {
        return ResponseEntity.ok(publicationService.poster(user, texte, media));
    }

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(publicationService.getPublications((Pageable) PageRequest.of(page, 5)));
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long id) {
        return ResponseEntity.ok(publicationService.toggleLike(user, id));
    }

    @PostMapping("/{id}/partager")
    public ResponseEntity<?> partager(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.incrementerPartage(id));
    }

    @GetMapping("/{id}/commentaires")
    public ResponseEntity<?> getCommentaires(@PathVariable Long id) {
        return ResponseEntity.ok(publicationService.getCommentaires(id));
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deletePub(@PathVariable Long id, @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(publicationService.supprimerPublication(id, user));
    }

    @PutMapping("/{id}/update-texte")
    public ResponseEntity<?> modifierTextePublication(
            @PathVariable Long id,
            @RequestParam String nouveauTexte,
            @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(publicationService.updateTexte(id, nouveauTexte, user));
    }


}

