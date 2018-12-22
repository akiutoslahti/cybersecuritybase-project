package sec.project.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.UserAccount;
import sec.project.repository.UserAccountRepository;

@Controller
public class RegisterController {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    public String getRegister(Model model) {
        return "register";
    }

    /*
    Vulnerabilities: A2 - Broken Authentication and A7 - Cross-Site Scripting(XSS)
    
    Passwords are not sanitized for weak passwords.
    
    Data entered to database is not sanitized and therefore sendin e.g. javascript
    to database is possible.
    
    FIX: Add sanitization to request parameters.
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String postRegister(Model model, @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password) {
        if (userAccountRepository.findByUsername(username) != null) {
            List<String> errors = new ArrayList<>();
            errors.add("Username already taken, please choose other.");
            model.addAttribute("errors", errors);
            return "register";
        }
        userAccountRepository.save(new UserAccount(username, name, password, "USER"));
        return "redirect:/login";
    }

}
