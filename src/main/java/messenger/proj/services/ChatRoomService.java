package messenger.proj.services;

import java.util.UUID;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



import messenger.proj.models.ChatRoom;
import messenger.proj.repositories.ChatRoomRepository;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {
	
	private final ChatRoomRepository chatRoomRep;

	@Autowired
	public ChatRoomService(ChatRoomRepository chatRoomRep) {
		this.chatRoomRep = chatRoomRep;
	}
	
	@Transactional
	public void save(String senderId, String recipientId) {
		
		Optional<ChatRoom> chatRoom = chatRoomRep.findBySenderIdAndRecipientId(senderId, recipientId);
		
		
		if (!chatRoom.isPresent()) {
			
			ChatRoom chatRoom1 = new ChatRoom();
			
			chatRoom1.setId(UUID.randomUUID().toString());
			chatRoom1.setSenderId(senderId);
			chatRoom1.setRecipientId(recipientId);
			chatRoomRep.save(chatRoom1);
		}
		
	}
	
	public Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId) {
		
		Optional<ChatRoom> chatRoom = chatRoomRep.findBySenderIdAndRecipientId(senderId, recipientId);
		
		return chatRoom;
	}
	
	
	
	

}
