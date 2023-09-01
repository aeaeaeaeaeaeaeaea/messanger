package messenger.proj.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import messenger.proj.models.ChatRoom;
import messenger.proj.services.ChatRoomService;

@Controller
public class ChatController {
	
	private final ChatRoomService chatRoomServ;
	
	@Autowired
	public ChatController(ChatRoomService chatRoomServ) {
		super();
		this.chatRoomServ = chatRoomServ;
	}
	
	@PostMapping("/chat")
	public String createChat(@RequestParam("userId") String userId, @RequestParam("currentUser") String currentUser) {
		
		chatRoomServ.save(userId, currentUser);
		
		Optional<ChatRoom> chat = chatRoomServ.findBySenderIdAndRecipientId(userId, currentUser);
		
		return "redirect:/chat/" + chat.get().getId();
	}
	
	
	

}
