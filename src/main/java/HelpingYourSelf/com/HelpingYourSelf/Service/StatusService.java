package HelpingYourSelf.com.HelpingYourSelf.Service;

import HelpingYourSelf.com.HelpingYourSelf.DTO.StatusDTO;
import HelpingYourSelf.com.HelpingYourSelf.Entity.Status;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import HelpingYourSelf.com.HelpingYourSelf.Repository.StatusRepository;
import HelpingYourSelf.com.HelpingYourSelf.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final CloudinaryService cloudinaryService;

@Transactional
public StatusDTO createStatus(Long userId, String text, MultipartFile[] files) throws IOException {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

    // Le reste du code reste inchangé
    Status status = Status.builder()
            .user(user)
            .text(text)
            .build();

    if (files != null && files.length > 0) {
        List<String> mediaUrls = cloudinaryService.uploadFiles(files);
        status.setMediaUrls(mediaUrls);
    }

    status = statusRepository.save(status);
    return convertToDTO(status);
}

/*    public List<StatusDTO> getStatusesForUser(Long currentUserId) {
        Instant now = Instant.now();
        List<Status> statuses = statusRepository.findStatusesFromContacts(currentUserId, now);
        return statuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }*/
@Transactional
public List<StatusDTO> getStatusesForUser(Long currentUserId) {
    Instant now = Instant.now();
    // Supprimer d'abord les statuts expirés
    statusRepository.deleteByExpiresAtBefore(now);

    // Récupérer uniquement les statuts non expirés
    List<Status> statuses = statusRepository.findStatusesFromContacts(currentUserId, now);
    return statuses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

/*    public List<StatusDTO> getUserStatuses(Long userId) {
        Instant now = Instant.now();
        List<Status> statuses = statusRepository.findActiveStatusesByUser(userId, now);
        return statuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }*/
@Transactional
public List<StatusDTO> getUserStatuses(Long userId) {
    Instant now = Instant.now();
    // Supprimer d'abord les statuts expirés
    statusRepository.deleteByExpiresAtBefore(now);

    // Récupérer uniquement les statuts non expirés
    List<Status> statuses = statusRepository.findActiveStatusesByUser(userId, now);
    return statuses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    private StatusDTO convertToDTO(Status status) {
        long hoursLeft = Duration.between(Instant.now(), status.getExpiresAt()).toHours();
        
        return StatusDTO.builder()
                .id(status.getId())
                .userId(status.getUser().getId())
                .userFullName(status.getUser().getPrenom() + " " + status.getUser().getNom())
                .userProfileImage(status.getUser().getProfileImage())
                .text(status.getText())
                .mediaUrls(status.getMediaUrls())
                .createdAt(status.getCreatedAt())
                .expiresAt(status.getExpiresAt())
                .timeLeftInHours(hoursLeft > 0 ? hoursLeft : 0)
                .build();
    }

    @Scheduled(fixedRate = 3600000) // Toutes les heures
    @Transactional
    public void deleteExpiredStatuses() {
        statusRepository.deleteByExpiresAtBefore(Instant.now());
    }
/*    public List<StatusDTO> getMyStatuses(Long userId) {
        List<Status> statuses = statusRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return statuses.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }*/
@Transactional
public List<StatusDTO> getMyStatuses(Long userId) {
    // Supprimer d'abord les statuts expirés
    statusRepository.deleteByExpiresAtBefore(Instant.now());

    // Récupérer uniquement les statuts non expirés
    List<Status> statuses = statusRepository.findByUserIdAndExpiresAtAfterOrderByCreatedAtDesc(
            userId,
            Instant.now()
    );
    return statuses.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}

    @Transactional
    public void deleteMyStatus(Long statusId, Long userId) {
        int deleted = statusRepository.deleteByIdAndUserId(statusId, userId);
        if (deleted == 0) {
            throw new RuntimeException("Statut non trouvé ou vous n'êtes pas autorisé à le supprimer");
        }
    }
}
