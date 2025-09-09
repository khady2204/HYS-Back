package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Status;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface StatusRepository extends JpaRepository<Status, Long> {
    
    @Query("SELECT s FROM Status s WHERE s.user.id = :userId AND s.expiresAt > :now")
    List<Status> findActiveStatusesByUser(@Param("userId") Long userId, @Param("now") Instant now);
    
    @Query("SELECT DISTINCT s FROM Status s " +
           "JOIN Message m ON (m.sender.id = s.user.id AND m.receiver.id = :currentUserId) " +
           "OR (m.receiver.id = s.user.id AND m.sender.id = :currentUserId) " +
           "WHERE s.expiresAt > :now AND s.user.id != :currentUserId")
    List<Status> findStatusesFromContacts(@Param("currentUserId") Long currentUserId, @Param("now") Instant now);
    
    /*void deleteByExpiresAtBefore(Instant now);*/
    @Modifying
    @Transactional
    @Query("DELETE FROM Status s WHERE s.expiresAt < :now")
    void deleteByExpiresAtBefore(@Param("now") Instant now);
    List<Status> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Modifying
    @Query("DELETE FROM Status s WHERE s.id = :statusId AND s.user.id = :userId")
    int deleteByIdAndUserId(@Param("statusId") Long statusId, @Param("userId") Long userId);

    List<Status> findByUserIdAndExpiresAtAfterOrderByCreatedAtDesc(
            @Param("userId") Long userId,
            @Param("now") Instant now
    );
}
