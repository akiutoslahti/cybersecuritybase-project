package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.UserAccount;
import sec.project.repository.UserAccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    UserAccountRepository userAccountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        UserAccount admin = new UserAccount("admin", "Anthony the Administrator", passwordEncoder.encode("admin12345"), "ADMIN");
        userAccountRepository.save(admin);
        UserAccount user = new UserAccount("user", "Umberto the User", passwordEncoder.encode("user12345"), "USER");
        userAccountRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByUsername(username);
        if (userAccount == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                userAccount.getUsername(),
                userAccount.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority(userAccount.getRole())));
    }

}
