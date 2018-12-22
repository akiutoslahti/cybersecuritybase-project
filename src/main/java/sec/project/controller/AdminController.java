package sec.project.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sec.project.domain.Signup;
import sec.project.domain.UserAccount;
import sec.project.repository.SignupRepository;
import sec.project.repository.UserAccountRepository;

@Controller
public class AdminController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    /*
    Vulnerability: A5 - Broken Access Control
    
    Every user regardless of their role can access /admin route.
    
    FIX: Check what is the role of authenticated user and permit only users
    with role ADMIN to access /admin route.
     */
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdmin(Authentication authentication, Model model) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName());
        model.addAttribute("name", account.getName());
        List<Signup> signups = signupRepository.findAll();
        model.addAttribute("signups", signups);
        return "admin";
    }
}
