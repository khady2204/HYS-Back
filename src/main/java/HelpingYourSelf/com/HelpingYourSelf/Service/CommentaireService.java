package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.CommentaireResponse;
import HelpingYourSelf.com.HelpingYourSelf.Entity.*;
import HelpingYourSelf.com.HelpingYourSelf.Repository.CommentaireRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.PublicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentaireService {
    private final PublicationRepository publicationRepo;
    private final CommentaireRepository commentaireRepo;
    private final NotificationService notificationService;

    @Transactional
    public CommentaireResponse commenter(User user, Long publicationId, String contenu, Long parentId) {
        Publication publication = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        Commentaire commentaire = new Commentaire();
        commentaire.setAuteur(user);
        commentaire.setPublication(publication);
        commentaire.setContenu(contenu);
        commentaire.setCreatedAt(Instant.now());

        if (parentId != null) {
            Commentaire parent = commentaireRepo.findById(parentId)
                    .orElseThrow(() -> new RuntimeException("Commentaire parent non trouvé"));
            commentaire.setParent(parent);
        }

        Commentaire saved = commentaireRepo.save(commentaire);

        // Envoyer une notification à l'auteur de la publication
        if (!publication.getAuteur().getId().equals(user.getId())) {
            notificationService.envoyerNotification(
                    Notification.builder()
                            .emetteur(user)
                            .destinataire(publication.getAuteur())
                            .message(user.getPrenom() + " a commenté votre publication")
                            .type(NotificationType.COMMENTAIRE)
                            .build()
            );
        }

        return mapToResponse(saved);
    }

    @Transactional
    public String toggleLike(User user, Long commentaireId) {
        Commentaire commentaire = commentaireRepo.findById(commentaireId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        boolean isLiked = commentaire.getLikes().stream()
                .anyMatch(u -> u.getId().equals(user.getId()));

        if (isLiked) {
            commentaire.getLikes().removeIf(u -> u.getId().equals(user.getId()));
        } else {
            commentaire.getLikes().add(user);

            // Envoyer une notification si ce n'est pas l'auteur qui like son propre commentaire
            if (!commentaire.getAuteur().getId().equals(user.getId())) {
                notificationService.envoyerNotification(
                        Notification.builder()
                                .emetteur(user)
                                .destinataire(commentaire.getAuteur())
                                .message(user.getPrenom() + " a aimé votre commentaire")
                                .type(NotificationType.LIKE_COMMENTAIRE)
                                .build()
                );
            }
        }

        commentaireRepo.save(commentaire);
        return isLiked ? "Like retiré" : "Commentaire aimé";
    }

    @Transactional(readOnly = true)
    public List<CommentaireResponse> getCommentairesAvecReponses(Long publicationId) {
        List<Commentaire> commentaires = commentaireRepo.findByPublicationIdAndParentIsNull(publicationId);
        return commentaires.stream()
                .map(this::mapToResponseWithReplies)
                .collect(Collectors.toList());
    }

    @Transactional
    public String supprimerCommentaire(Long commentaireId, User user) {
        Commentaire commentaire = commentaireRepo.findById(commentaireId)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé"));

        if (!commentaire.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer ce commentaire");
        }

        commentaireRepo.delete(commentaire);
        return "Commentaire supprimé avec succès";
    }

    // Méthodes utilitaires
    private CommentaireResponse mapToResponse(Commentaire commentaire) {
        return CommentaireResponse.builder()
                .id(commentaire.getId())
                .contenu(commentaire.getContenu())
                .auteurId(commentaire.getAuteur().getId())
                .auteurNom(commentaire.getAuteur().getPrenom() + " " + commentaire.getAuteur().getNom())
                .createdAt(commentaire.getCreatedAt())
                .likesCount(commentaire.getLikes().size())
                .reponses(new ArrayList<>()) // Les réponses seront ajoutées récursivement si nécessaire
                .build();
    }

    private CommentaireResponse mapToResponseWithReplies(Commentaire commentaire) {
        CommentaireResponse response = mapToResponse(commentaire);

        // Récupérer les réponses de manière récursive
        List<CommentaireResponse> reponses = commentaireRepo.findByParent(commentaire).stream()
                .map(this::mapToResponseWithReplies)
                .collect(Collectors.toList());

        response.setReponses(reponses);
        return response;
    }
}