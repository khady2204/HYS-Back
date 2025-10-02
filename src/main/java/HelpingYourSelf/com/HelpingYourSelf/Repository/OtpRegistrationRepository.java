package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.OtpRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface OtpRegistrationRepository extends JpaRepository<OtpRegistration, Long> {

    Optional<OtpRegistration> findByEmailAndOtpCode(String email, String otpCode);
    Optional<OtpRegistration> findByEmail(String email);

    @Modifying
    @Query("DELETE FROM OtpRegistration o WHERE o.otpExpiration < :now")
    void deleteExpiredOtps(@Param("now") Instant now);

    void deleteByEmail(String email);
}