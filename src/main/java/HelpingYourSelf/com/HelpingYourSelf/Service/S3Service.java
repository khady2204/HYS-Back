package HelpingYourSelf.com.HelpingYourSelf.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;


@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name:hsycompartiment}")
    private String bucketName;

    @Value("${aws.s3.profiles-folder:profiles}")
    private String profilesFolder;

    @Value("${aws.s3.status-folder:status_media}")
    private String statusFolder;

    @Value("${aws.s3.publications-folder:publications}")
    private String publicationsFolder;

    @Value("${aws.s3.messages-folder:messages}")
    private String messagesFolder;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
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
        return s3Client.utilities()
                .getUrl(GetUrlRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build())
                .toExternalForm();
    }

    public String uploadProfileImage(MultipartFile file) {
        return uploadFile(file, profilesFolder);
    }

    public String uploadStatusMedia(MultipartFile file) {
        return uploadFile(file, statusFolder);
    }

    public String uploadPublicationMedia(MultipartFile file) {
        return uploadFile(file, publicationsFolder);
    }

    public String uploadMessageMedia(MultipartFile file) {
        return uploadFile(file, messagesFolder);
    }
}
