package HelpingYourSelf.com.HelpingYourSelf.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${upload.path}")
    private String uploadDir;

    public String saveProfileImage(MultipartFile file) {
        try {
            String uploadDir = "uploads/profiles/";
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, filename);
            Files.createDirectories(filePath.getParent());
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/profiles/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de la photo", e);
        }
    }
}
