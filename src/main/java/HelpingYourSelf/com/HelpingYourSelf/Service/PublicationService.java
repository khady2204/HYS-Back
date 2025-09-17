package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.CommentaireResponse;
import HelpingYourSelf.com.HelpingYourSelf.DTO.MediaDTO;
import HelpingYourSelf.com.HelpingYourSelf.DTO.PublicationDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.*;
import HelpingYourSelf.com.HelpingYourSelf.Repository.CommentaireRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.MediaRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.PublicationRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PublicationService {
    private final PublicationRepository publicationRepo;
    private final CommentaireRepository commentaireRepo;
    private final MediaRepository mediaRepo;
    private final UserRepository userRepo;
    private final CloudinaryService cloudinaryService;
    private final NotificationService notificationService;

    @Transactional
    public PublicationDTO poster(User auteur, String texte, List<MultipartFile> fichiers, List<String> descriptions) {
        // Créer la publication
        Publication publication = Publication.builder()
                .auteur(auteur)
                .texte(texte)
                .build();

        publication = publicationRepo.save(publication);

        // Gestion des médias
        if (fichiers != null && !fichiers.isEmpty()) {
            try {
                List<String> urls = cloudinaryService.uploadFiles(fichiers.toArray(new MultipartFile[0]));
                for (int i = 0; i < urls.size(); i++) {
                    Media media = Media.builder()
                            .url(urls.get(i))
                            .type(fichiers.get(i).getContentType().startsWith("video/") ? "video" : "image")
                            .description(descriptions != null && i < descriptions.size() ? descriptions.get(i) : null)
                            .publication(publication)
                            .build();
                    mediaRepo.save(media);
                }
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du téléchargement des fichiers vers Cloudinary", e);
            }
        }

        return mapToDTO(publication);
    }

    @Transactional(readOnly = true)
    public Page<PublicationDTO> getPublications(Pageable pageable) {
        return publicationRepo.findAllByOrderByCreatedAtDesc(pageable)
                .map(this::mapToDTO);
    }

    @Transactional
    public String toggleLike(User user, Long publicationId) {
        // Get the publication
        Publication publication = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));
                
        // Check if user is the author
        boolean isAuthor = publication.getAuteur().getId().equals(user.getId());
        
        // Toggle like
        int currentLikes = publication.getNombreLikes();
        boolean isLiked = currentLikes > 0; // Simple check for this example
        
        if (isLiked) {
            publication.setNombreLikes(currentLikes - 1);
        } else {
            publication.setNombreLikes(currentLikes + 1);
            
            // Send notification if not the author
            if (!isAuthor) {
                notificationService.envoyerNotification(
                        Notification.builder()
                                .emetteur(user)
                                .destinataire(publication.getAuteur())
                                .message(user.getPrenom() + " a aimé votre publication")
                                .type(NotificationType.LIKE)
                                .build()
                );
            }
        }
        
        publicationRepo.save(publication);
        return isLiked ? "Like retiré" : "Publication aimée";
    }

    @Transactional
    public String incrementerPartage(Long publicationId, User user) {
        Publication publication = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        publication.setNombrePartages(publication.getNombrePartages() + 1);
        publicationRepo.save(publication);

        // Notifier l'auteur de la publication
        if (!publication.getAuteur().getId().equals(user.getId())) {
            notificationService.envoyerNotification(
                    Notification.builder()
                            .emetteur(user)
                            .destinataire(publication.getAuteur())
                            .message(user.getPrenom() + " a partagé votre publication")
                            .type(NotificationType.PARTAGE)
                            .build()
            );
        }

        return "Publication partagée avec succès";
    }

    @Transactional(readOnly = true)
    public List<Commentaire> getCommentaires(Long publicationId) {
        return commentaireRepo.findByPublicationIdAndParentIsNull(publicationId);
    }

    @Transactional
    public String supprimerPublication(Long publicationId, User user) {
        Publication publication = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        if (!publication.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à supprimer cette publication");
        }

        publicationRepo.delete(publication);
        return "Publication supprimée avec succès";
    }

    @Transactional
    public String updateTexte(Long publicationId, String nouveauTexte, User user) {
        Publication publication = publicationRepo.findById(publicationId)
                .orElseThrow(() -> new RuntimeException("Publication non trouvée"));

        if (!publication.getAuteur().getId().equals(user.getId())) {
            throw new AccessDeniedException("Vous n'êtes pas autorisé à modifier cette publication");
        }

        publication.setTexte(nouveauTexte);
        publicationRepo.save(publication);

        // Notifier les abonnés
        notificationService.notifierAbonnes(
                publication.getAuteur(),
                publication.getAuteur().getAbonnes(),
                publication.getAuteur().getPrenom() + " a mis à jour sa publication",
                NotificationType.MISE_A_JOUR_PUBLICATION
        );

        return "Publication mise à jour avec succès";
    }

    // Méthodes utilitaires
    private PublicationDTO mapToDTO(Publication publication) {
        return PublicationDTO.builder()
                .id(publication.getId())
                .texte(publication.getTexte())
                .auteurNom(publication.getAuteur().getPrenom() + " " + publication.getAuteur().getNom())
                .createdAt(publication.getCreatedAt())
                .nombreCommentaires(publication.getCommentaires() != null ? publication.getCommentaires().size() : 0)
                .nombreLikes(publication.getNombreLikes())
                .nombrePartages(publication.getNombrePartages())
                .medias(publication.getMedias() != null ? mapMediasToDTO(publication.getMedias()) : new ArrayList<>())
                .commentaires(publication.getCommentaires() != null ? mapCommentairesToResponse(publication.getCommentaires()) : new ArrayList<>())
                .build();
    }

    private List<MediaDTO> mapMediasToDTO(List<Media> medias) {
        if (medias == null) return new ArrayList<>();

        return medias.stream()
                .map(media -> MediaDTO.builder()
                        .url(media.getUrl())
                        .type(media.getType())
                        .description(media.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    private List<CommentaireResponse> mapCommentairesToResponse(List<Commentaire> commentaires) {
        if (commentaires == null) return new ArrayList<>();

        return commentaires.stream()
                .filter(commentaire -> commentaire.getParent() == null) // Uniquement les commentaires principaux
                .map(commentaire -> {
                    CommentaireResponse response = new CommentaireResponse();
                    response.setId(commentaire.getId());
                    response.setContenu(commentaire.getContenu());
                    response.setAuteurNom(commentaire.getAuteur().getPrenom() + " " + commentaire.getAuteur().getNom());
                    response.setAuteurId(commentaire.getAuteur().getId());
                    response.setCreatedAt(commentaire.getCreatedAt());
                    response.setLikesCount(commentaire.getLikes().size());

                    // Récupérer les réponses
                    List<Commentaire> reponses = commentaireRepo.findByParent(commentaire);
                    response.setReponses(mapCommentairesToResponse(reponses));

                    return response;
                })
                .collect(Collectors.toList());
    }
}