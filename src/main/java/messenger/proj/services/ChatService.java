package messenger.proj.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import messenger.proj.repositories.ChatRepository;

@Service
public class ChatService {
	
	private ChatRepository chatRep;
	
	@Autowired
	public ChatService(ChatRepository chatRep) {
		this.chatRep = chatRep;
	}
	

	
	

}
