package HelpingYourSelf.com.HelpingYourSelf.Repository;

import HelpingYourSelf.com.HelpingYourSelf.Entity.Role;
import HelpingYourSelf.com.HelpingYourSelf.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhone(String phone);
    Optional<User> findByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmail(String email);
    List<User> findByRolesContaining(Role role);
    List<User> findByCreatedBy(User createdBy);
    List<User> findByIsOnlineTrue();


    Optional<User> findByNomAndPrenom(String nom, String prenom);





}
