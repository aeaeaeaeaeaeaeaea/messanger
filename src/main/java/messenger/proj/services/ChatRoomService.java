package messenger.proj.services;

import java.util.UUID;

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
	public void save(ChatRoom chatRoom) {
		chatRoom.setId(UUID.randomUUID().toString());
		chatRoomRep.save(chatRoom);
	}
	
	
	
	

}
