package sec.project.controller;

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
public class UserController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getAdmin(Authentication authentication, Model model) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName());
        Signup ownSignup = signupRepository.findByName(account.getName());
        model.addAttribute("name", account.getName());
        if (ownSignup != null) {
            model.addAttribute("registered", true);
        } else {
            model.addAttribute("registered", false);
        }
        return "user";
    }
}
