package massenger.proj.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import massenger.proj.models.message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import massenger.proj.services.MessageService;
import massenger.proj.services.UserService;

@Controller()
@RequestMapping("/message")
public class MessageController {

	private UserService userServ;

	public MessageController(UserService userServ) {
		this.userServ = userServ;
	}

	@GetMapping()
	public String message(Model model) {
		model.addAttribute("users", userServ.findAll());
		return "message1";
	}

}
