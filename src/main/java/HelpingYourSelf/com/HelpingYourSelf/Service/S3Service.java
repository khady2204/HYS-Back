package HelpingYourSelf.com.HelpingYourSelf.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Service
public class S3Service {
    @Value("${aws.s3.bucket-name: :hsycompartiment}")
    private String bucketName;

    private final S3Client s3Client;

    public S3Service() {
        this.s3Client = S3Client.builder()
                .region(software.amazon.awssdk.regions.Region.AF_SOUTH_1)
                .build();
    }

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

    private String generatePublicUrl(String fileName) {
        return String.format("https://%s.s3.af-south-1.amazonaws.com/%s", bucketName, fileName);
    }

    public String uploadProfileImage(MultipartFile file) {
        return uploadFile(file, "profiles");
    }

    public String uploadStatusMedia(MultipartFile file) {
        return uploadFile(file, "status_media");
    }

    public String uploadPublicationMedia(MultipartFile file) {
        return uploadFile(file, "publications");
    }
}
