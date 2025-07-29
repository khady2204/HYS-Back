package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.CommentaireDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Commentaire;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Publication;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.CommentaireRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentaireService {

    @Autowired
    private PublicationRepository publicationRepo;

    @Autowired
    private CommentaireRepository commentaireRepo;

    /**
     * Ajouter un commentaire (ou réponse à un commentaire)
     */
    public CommentaireDTO commenter(User user, Long publicationId, String contenu, Long parentId) {
        Publication pub = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        Commentaire commentaire = new Commentaire();
        commentaire.setAuteur(user);
        commentaire.setPublication(pub);
        commentaire.setContenu(contenu);
        commentaire.setCreatedAt(Instant.now());

        if (parentId != null) {
            Commentaire parent = commentaireRepo.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Commentaire parent non trouvé"));
            commentaire.setParent(parent);
        }

        commentaire = commentaireRepo.save(commentaire);
        return mapToDTO(commentaire);
    }

    /**
     * Liker ou disliker un commentaire
     */
    public String toggleLike(User user, Long id) {
        Commentaire commentaire = commentaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (commentaire.getLikes().contains(user)) {
            commentaire.getLikes().remove(user);
        } else {
            commentaire.getLikes().add(user);
        }

        commentaireRepo.save(commentaire);
        return "Like sur commentaire mis à jour.";
    }

    /**
     * Récupérer tous les commentaires racines (sans parent) d’une publication
     */
    public List<CommentaireDTO> getCommentairesAvecReponses(Long pubId) {
        List<Commentaire> commentaires = commentaireRepo.findByPublicationIdAndParentIsNull(pubId);
        return commentaires.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Supprimer un commentaire s’il est de l’auteur connecté
     */
    public String supprimerCommentaire(Long id, User user) {
        Commentaire c = commentaireRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!c.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas supprimer ce commentaire.");
        }

        commentaireRepo.delete(c);
        return "Commentaire supprimé.";
    }

    /**
     * Convertir un commentaire (et ses réponses) en DTO
     */
    private CommentaireDTO mapToDTO(Commentaire commentaire) {
        List<CommentaireDTO> reponsesDTO = commentaire.getReponses() != null
                ? commentaire.getReponses().stream().map(this::mapToDTO).toList()
                : List.of();

        return new CommentaireDTO(
                commentaire.getId(),
                commentaire.getContenu(),
                commentaire.getAuteur().getPrenom() + " " + commentaire.getAuteur().getNom(),
                commentaire.getCreatedAt(),
                commentaire.getLikes() != null ? commentaire.getLikes().size() : 0,
                reponsesDTO
        );
    }
}
