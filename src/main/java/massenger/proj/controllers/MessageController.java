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

@Controller()
@RequestMapping("/message")
public class MessageController {
	
	private final MessageService messageServ;
	
	public MessageController(MessageService messageServ) {
		this.messageServ = messageServ;
	}
	
	@GetMapping()
	public String message(Model model) {
		model.addAttribute("messages", messageServ.findAll());
		System.out.println("Я ТУТ!");
		return "message1";
	}
	
	@PostMapping("/post")
	public String mess(@RequestParam("message") String message) {
		System.out.println("Test: " + message);
		message mess = new message();
		mess.setMessage(message);
		messageServ.save(mess);
		return "redirect:/message";
	}

}
