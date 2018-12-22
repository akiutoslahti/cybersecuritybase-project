package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.domain.UserAccount;
import sec.project.repository.SignupRepository;
import sec.project.repository.UserAccountRepository;

@Controller
public class FormController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Authentication authentication, Model model) {
        UserAccount account = userAccountRepository.findByUsername(authentication.getName());
        model.addAttribute("name", account.getName());
        return "form";
    }

    /*
    Vulnerability: A7 - Cross-Site Scripting(XSS)
    
    Data entered to database is not sanitized and therefore sendin e.g. javascript
    to database is possible.
   
    FIX: Add sanitization to request parameters.
     */
    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name,
            @RequestParam String address,
            @RequestParam String mobile,
            @RequestParam String misc) {
        signupRepository.save(new Signup(name, address, mobile, misc));
        return "redirect:/user";
    }

}
