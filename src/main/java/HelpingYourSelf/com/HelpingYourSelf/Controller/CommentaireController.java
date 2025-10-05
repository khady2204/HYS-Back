package HelpingYourSelf.com.HelpingYourSelf.Controller;


import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Service.CommentaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/publications")
public class CommentaireController {

    @Autowired
    private CommentaireService commentaireService;

    @PostMapping("/{publicationId}/commenter")
    public ResponseEntity<?> commenter(@AuthenticationPrincipal(expression = "user") User user,
                                     @PathVariable Long publicationId,
                                     @RequestBody Map<String, String> requestBody) {
        String contenu = requestBody.get("contenu");
        Long parentId = requestBody.get("parentId") != null ?
                Long.parseLong(requestBody.get("parentId")) : null;

        return ResponseEntity.ok(commentaireService.commenter(user, publicationId, contenu, parentId));
    }


    @PostMapping("/commentaires/{commentId}/like")
    public ResponseEntity<?> likeComment(@AuthenticationPrincipal(expression = "user") User user, 
                                      @PathVariable("commentId") Long commentId) {
        return ResponseEntity.ok(commentaireService.toggleLike(user, commentId));
    }

    @DeleteMapping("/commentaires/{commentId}")
    public ResponseEntity<?> deleteCom(@PathVariable("commentId") Long commentId, 
                                    @AuthenticationPrincipal(expression = "user") User user) {
        return ResponseEntity.ok(commentaireService.supprimerCommentaire(commentId, user));
    }

}

