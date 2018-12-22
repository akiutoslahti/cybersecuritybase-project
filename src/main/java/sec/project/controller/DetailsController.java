package sec.project.controller;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Controller
public class DetailsController {

    @Autowired
    private EntityManager entityManager;

    /*
    Vulnerability: A1 - Injection
    
    Executing RAW sql query combined with parameter concatenation makes SQL
    injections possible.
    
    FIX: Add autowire SignupRepository to class and execute detail query through
    hibernate like done in every other class.
     */
    @RequestMapping(value = "/details", method = RequestMethod.GET)
    public String details(@RequestParam String query, Model model) {

        String queryStr = "select * from signup where name = '" + query + "'";
        List<Signup> signup = null;

        try {
            Query sqlQuery = entityManager.createNativeQuery(queryStr);
            signup = sqlQuery.getResultList();
        } catch (Exception e) {
        }

        model.addAttribute("details", signup);
        model.addAttribute("name", query);
        return "details";
    }

}
