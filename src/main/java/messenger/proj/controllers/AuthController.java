package messenger.proj.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import messenger.proj.models.User;
import messenger.proj.services.UserService;

@Controller
@RequestMapping("/auth")
public class AuthController {
	
	private UserService userServ;
	
	public AuthController(UserService userServ) {
		this.userServ = userServ;
	}


    @GetMapping("/login")
    public String auth() {
        return "auth/login";
    }
    
    @GetMapping("/reg")
    public String reg(Model model) {
    	model.addAttribute("user", new User());
    	return "auth/reg";
    }
    
    @PostMapping("/register")
    public String registration(@ModelAttribute("user") User user) {
    	userServ.save(user);
    	return "redirect:/auth/login";
    }


}
