package largescaleit_demo.controllers;

import java.util.ArrayList;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@Controller
public class HomeController {
	
	private final GitLabController gitLabController;
	 public HomeController(GitLabController gitLabController) {
	        this.gitLabController = gitLabController;
	    }
	 	@GetMapping("/error")
	    public String handleError() {
	        return "welcome"; 
	    }
	 
	 @GetMapping("/")
	 public String home(Model model, OAuth2AuthenticationToken authentication, HttpSession session) {
	     Boolean initialViewShown = (Boolean) session.getAttribute("initialViewShown");

	     if (initialViewShown == null || !initialViewShown) {
	         session.setAttribute("initialViewShown", true);

	         return "welcome";
	     }

	     var userAttributes = authentication.getPrincipal().getAttributes();
	     ArrayList<String> group = authentication.getPrincipal().getAttribute("https://gitlab.org/claims/groups/owner");
	     String name = authentication.getPrincipal().getAttribute("name");
	     String email = authentication.getPrincipal().getAttribute("email");
	     System.out.println(email);
	     System.out.println(name);
	     System.out.println("--------------------------");

	     session.setAttribute("userName", name);
	     session.setAttribute("userEmail", email);
	     String name1 = (String) session.getAttribute("userName");
	     String email1 = (String) session.getAttribute("userEmail");

	     model.addAttribute("name2", name1);
	     model.addAttribute("email2", email1);
	     boolean hasGroup = group.contains("scalableit-2024/roles/onlineshop");

	     if (hasGroup) {
	         int memberCount = gitLabController.getMemberCount();
	         model.addAttribute("memberCount", memberCount);
	         return "adminHome";
	     } else {
	         model.addAttribute("title", "Coffee Shop");
	         System.out.println("user" +
	             userAttributes.entrySet().parallelStream().collect(
	                 StringBuilder::new,
	                 (s, e) -> s.append(e.getKey()).append(": ").append(e.getValue()),
	                 (a, b) -> a.append("\n").append(b)
	             ) +
	             " go !");
	         return "Home";
	     }
	 }
	
	 @GetMapping("/logout")
	    public String logout() {
	        return "welcome"; // 
	    }
	 

    @GetMapping("/userClient")
    public String userClient() {
        return "userClient"; 
    }
}

