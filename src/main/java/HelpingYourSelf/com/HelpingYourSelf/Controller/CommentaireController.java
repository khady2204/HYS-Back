package HelpingYourSelf.com.HelpingYourSelf.Controller;


import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.CommentaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commentaires")
public class CommentaireController {

    @Autowired
    private CommentaireService commentaireService;

    @PostMapping("/{publicationId}/ajouter")
    public ResponseEntity<?> commenter(@AuthenticationPrincipal(expression = "user") User user,
                                       @PathVariable Long publicationId,
                                       @RequestParam String contenu,
                                       @RequestParam(required = false) Long parentId) {
        return ResponseEntity.ok(commentaireService.commenter(user, publicationId, contenu, parentId));
    }


    @PostMapping("/{id}/like")
    public ResponseEntity<?> like(@AuthenticationPrincipal(expression = "user") User user, @PathVariable Long id) {
        return ResponseEntity.ok(commentaireService.toggleLike(user, id));
    }

    @DeleteMapping("/{id}/supprimer")
    public ResponseEntity<?> deleteCom(@PathVariable Long id, @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(commentaireService.supprimerCommentaire(id, user));
    }

}

