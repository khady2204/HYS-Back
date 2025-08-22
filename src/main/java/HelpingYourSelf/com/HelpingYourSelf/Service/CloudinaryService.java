package HelpingYourSelf.com.HelpingYourSelf.Service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;

@Service
public class CloudinaryService {
    private final Cloudinary cloudinary;

    public CloudinaryService() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "du7libmu5");
        config.put("api_key", "835729119528394");
        config.put("api_secret", "LkGcpDACPOONBo9sEb6fScZj5ag");
        config.put("secure", "true");
        this.cloudinary = new Cloudinary(config);
    }

    public String uploadFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }

        try {
            // Détecter le type de contenu
            String contentType = file.getContentType();
            
            Map<String, Object> options = new HashMap<>();
            options.put("folder", "status_media");

            // Si c'est une vidéo, on définit explicitement le type de ressource
            if (contentType != null && contentType.startsWith("video/")) {
                options.put("resource_type", "video");
                options.put("chunk_size", 6000000); // 6MB chunks
                options.put("timeout", 120000); // 2 minutes timeout
                // Pour les vidéos, on ne met pas de transformation eager
            } else {
                // Pour les images, on utilise la détection automatique
                options.put("resource_type", "auto");
            }

            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
            return (String) uploadResult.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'upload du fichier: " + e.getMessage() +
                    " | Type: " + (file.getContentType() != null ? file.getContentType() : "inconnu"), e);
        }
    }

    public List<String> uploadFiles(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String url = uploadFile(file);
                    urls.add(url);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'upload d'un fichier: " + e.getMessage());
                    // Logger l'erreur complète pour le débogage
                    e.printStackTrace();
                }
            }
        }
        return urls;
    }
}
