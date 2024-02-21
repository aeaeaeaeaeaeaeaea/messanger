package messenger.proj.controllers;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import messenger.proj.models.User;
import messenger.proj.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private UserService userServ;
	
	public AuthController(UserService userServ) {
		this.userServ = userServ;
	}
    
    @PostMapping("/register")
    public String registration(@RequestBody User user) {
    	userServ.save(user);
    	return "redirect:/auth/login";
    }


}
