package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Interet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteretRepository extends JpaRepository<Interet, Long> {
    // Pas besoin de redéclarer findAllById, elle est héritée


}
