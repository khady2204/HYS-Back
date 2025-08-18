package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Page<Publication> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
