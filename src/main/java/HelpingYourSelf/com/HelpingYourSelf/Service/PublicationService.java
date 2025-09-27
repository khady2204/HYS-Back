package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicationDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.*;
import HelpingYourSelf.com.HelpingYourSelf.Repository.CommentaireRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Set;

@Service
public class PublicationService {

    @Autowired
    private PublicationRepository publicationRepo;

    @Autowired
    private CommentaireRepository commentaireRepo;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private S3Service s3Service;

    public PublicationDTO poster(User auteur, String texte, MultipartFile media) {
        Publication pub = new Publication();
        pub.setAuteur(auteur);
        pub.setTexte(texte);
        // Remplacement de l'ancienne méthode par S3
        if (media != null && !media.isEmpty()) {
            String mediaUrl = s3Service.uploadFile(media, "publications");
            pub.setMediaUrl(mediaUrl);
            pub.setMediaType(media.getContentType().split("/")[0]);
        } else {
            pub.setMediaUrl(null);
            pub.setMediaType(null);
        }

        publicationRepo.save(pub);
        return mapToDTO(pub);


    }

    public Page<PublicationDTO> getPublications(Pageable pageable) {
        return publicationRepo.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDTO);
    }

    public String toggleLike(User user, Long id) {
        Publication pub = publicationRepo.findById(id).orElseThrow();
        if (pub.getLikes().contains(user)) {
            pub.getLikes().remove(user);
        } else {
            pub.getLikes().add(user);

            // 🔔 Notification pour l’auteur
            if (!pub.getAuteur().getId().equals(user.getId())) {
                notificationService.envoyerNotification(Notification.builder()
                        .emetteur(user)
                        .destinataire(pub.getAuteur())
                        .message(user.getPrenom() + " a aimé votre publication.")
                        .type(NotificationType.LIKE)
                        .build());
            }
        }
        publicationRepo.save(pub);
        return "Like toggled";
    }

    public String incrementerPartage(Long id) {
        Publication pub = publicationRepo.findById(id).orElseThrow();
        pub.setNombrePartages(pub.getNombrePartages() + 1);
        publicationRepo.save(pub);
        return "Partagé avec succès.";
    }

    public List<Commentaire> getCommentaires(Long pubId) {
        return commentaireRepo.findByPublicationIdAndParentIsNull(pubId);
    }

    private PublicationDTO mapToDTO(Publication pub) {
        return new PublicationDTO(
                pub.getId(),
                pub.getTexte(),
                pub.getMediaUrl(),
                pub.getMediaType(),
                pub.getAuteur().getPrenom() + " " + pub.getAuteur().getNom(),
                pub.getCreatedAt(),
                pub.getCommentaires().size(),
                pub.getLikes().size(),
                pub.getNombrePartages()
        );
    }

    public String supprimerPublication(Long id, User user) {
        Publication pub = publicationRepo.findById(id).orElseThrow();
        if (!pub.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous ne pouvez pas supprimer cette publication.");
        }
        publicationRepo.delete(pub);
        return "Publication supprimée.";
    }


    public String updateTexte(Long id, String nouveauTexte, User user) {
        Publication pub = publicationRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Publication introuvable"));

        if (!pub.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'avez pas le droit de modifier cette publication.");
        }

        pub.setTexte(nouveauTexte);
        publicationRepo.save(pub);

        // 🔔 Notification à tous les followers
        Set<User> followers = user.getFollowers();
        for (User follower : followers) {
            notificationService.envoyerNotification(Notification.builder()
                    .emetteur(user)
                    .destinataire(follower)
                    .message(user.getPrenom() + " a mis à jour une de ses publications.")
                    .type(NotificationType.MISE_A_JOUR_PUBLICATION)
                    .build());
        }

        return "Texte de la publication mise à jour avec succès.";
    }




}
