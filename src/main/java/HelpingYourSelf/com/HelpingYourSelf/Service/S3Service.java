package HelpingYourSelf.com.HelpingYourSelf.Service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class S3Service {

    @Value("${aws.s3.bucket-name:hsycompartiment}")
    private String bucketName;

    private final S3Client s3Client;

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.AF_SOUTH_1)
                .build();
    }

    // MÉTHODE EXISTANTE
    public String uploadFile(MultipartFile file, String folder) {
        try {
            String fileName = folder + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(request, RequestBody.fromBytes(file.getBytes()));
            return generatePublicUrl(fileName);

        } catch (Exception e) {
            throw new RuntimeException("Erreur upload S3: " + e.getMessage());
        }
    }

    // NOUVELLE MÉTHODE - Équivalente à Cloudinary
    public List<String> uploadFiles(MultipartFile[] files, String folder) {
        if (files == null || files.length == 0) {
            return new ArrayList<>();
        }

        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                try {
                    String url = uploadFile(file, folder);
                    urls.add(url);
                    System.out.println("✅ Fichier uploadé avec succès: " + url);
                } catch (Exception e) {
                    System.err.println("❌ Erreur lors de l'upload d'un fichier: " + e.getMessage());
                    // Logger l'erreur mais continuer avec les autres fichiers
                    e.printStackTrace();
                }
            }
        }
        return urls;
    }

    // SURCHARGE pour utiliser "status_media" par défaut (comme Cloudinary)
    public List<String> uploadFiles(MultipartFile[] files) {
        return uploadFiles(files, "status_media");
    }

    private String generatePublicUrl(String fileName) {
        return String.format("https://%s.s3.af-south-1.amazonaws.com/%s", bucketName, fileName);
    }

    // MÉTHODES SPÉCIALISÉES EXISTANTES
    public String uploadProfileImage(MultipartFile file) {
        return uploadFile(file, "profiles");
    }

    public String uploadStatusMedia(MultipartFile file) {
        return uploadFile(file, "status_media");
    }

    public String uploadPublicationMedia(MultipartFile file) {
        return uploadFile(file, "publications");
    }

    public String uploadMessageMedia(MultipartFile file) {
        return uploadFile(file, "messages");
    }

    // NOUVELLES MÉTHODES SPÉCIALISÉES POUR MULTIPLES FICHIERS
    public List<String> uploadProfileImages(MultipartFile[] files) {
        return uploadFiles(files, "profiles");
    }

    public List<String> uploadStatusMedias(MultipartFile[] files) {
        return uploadFiles(files, "status_media");
    }

    public List<String> uploadPublicationMedias(MultipartFile[] files) {
        return uploadFiles(files, "publications");
    }
}
