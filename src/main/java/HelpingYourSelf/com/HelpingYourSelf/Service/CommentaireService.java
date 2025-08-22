package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.CommentaireDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Commentaire;
import HelpingYourSelf.com.HelpingYourSelf.Entity.NotificationType;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Publication;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.CommentaireRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final PublicationRepository publicationRepo;
    private final CommentaireRepository commentaireRepo;
    private final NotificationService notificationService;

    public Commentaire commenter(User user, Long publicationId, String contenu, Long parentId) {
        Publication pub = publicationRepo.findById(publicationId).orElseThrow();
        Commentaire commentaire = new Commentaire();
        commentaire.setAuteur(user);
        commentaire.setPublication(pub);
        commentaire.setContenu(contenu);

        if (parentId != null) {
            Commentaire parent = commentaireRepo.findById(parentId).orElseThrow();
            commentaire.setParent(parent);
        }

        Commentaire saved = commentaireRepo.save(commentaire);

        // Notification à l’auteur de la publication
        if (!pub.getAuteur().getId().equals(user.getId())) {
            notificationService.creerNotification(
                    user,
                    pub.getAuteur(),
                    "Nouveau commentaire sur votre publication.",
                    NotificationType.COMMENTAIRE,
                    "/publications/" + pub.getId() + "/commentaires"
            );
        }



        return saved;
    }

    public String toggleLike(User user, Long id) {
        Commentaire commentaire = commentaireRepo.findById(id).orElseThrow();
        if (commentaire.getLikes().contains(user)) {
            commentaire.getLikes().remove(user);
        } else {
            commentaire.getLikes().add(user);
        }
        commentaireRepo.save(commentaire);
        return "Like sur commentaire mis à jour.";
    }

    public List<Commentaire> getCommentairesAvecReponses(Long pubId) {
        List<Commentaire> commentaires = commentaireRepo.findByPublicationIdAndParentIsNull(pubId);
        for (Commentaire c : commentaires) {
            c.setReponses(commentaireRepo.findByParent(c));
        }
        return commentaires;
    }

    private CommentaireDTO mapToDTO(Commentaire commentaire) {
        List<CommentaireDTO> reponses = commentaire.getReponses() != null
                ? commentaire.getReponses().stream().map(this::mapToDTO).toList()
                : List.of();

        return new CommentaireDTO(
                commentaire.getId(),
                commentaire.getContenu(),
                commentaire.getAuteur().getPrenom() + " " + commentaire.getAuteur().getNom(),
                commentaire.getCreatedAt(),
                commentaire.getLikes().size(),
                reponses
        );
    }

    public String supprimerCommentaire(Long id, User user) {
        Commentaire c = commentaireRepo.findById(id).orElseThrow();
        if (!c.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas supprimer ce commentaire.");
        }
        commentaireRepo.delete(c);
        return "Commentaire supprimé.";
    }
}
