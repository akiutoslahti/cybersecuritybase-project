package sec.project.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import sec.project.domain.Signup;
import sec.project.domain.UserAccount;
import sec.project.repository.SignupRepository;
import sec.project.repository.UserAccountRepository;

@Controller
public class DefaultController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @RequestMapping("*")
    public String defaultMapping(Authentication authentication) {
        boolean isAdmin = false;
        for (GrantedAuthority ga : authentication.getAuthorities()) {
            if (ga.getAuthority().equals("ADMIN")) {
                return "redirect:/admin";
            }
        }
        return "redirect:/user";
    }

    @PostConstruct
    public void init() {
        try {
            Scanner sc = new Scanner(Paths.get("fake_signups.txt"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] pieces = line.split(";");
                String username = pieces[0];
                String password = pieces[1];
                String name = pieces[2];
                String address = pieces[3];
                String misc = pieces[4];
                userAccountRepository.save(new UserAccount(username, name, passwordEncoder.encode(password), "USER"));
                signupRepository.save(new Signup(name, address, fakeMobile(), misc));
            }
        } catch (IOException ioe) {
            Logger.getLogger(FormController.class.getName()).log(Level.SEVERE, null, ioe);
        }
    }

    private String fakeMobile() {
        Random rng = new Random();
        String[] prefix = {"040", "041", "045", "050"};
        String ret = prefix[rng.nextInt(4)] + "-";
        for (int i = 0; i < 7; i++) {
            ret += rng.nextInt(9);
        }
        return ret;
    }

}
