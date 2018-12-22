package sec.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sec.project.domain.UserAccount;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    UserAccount findByUsername(String username);

}
