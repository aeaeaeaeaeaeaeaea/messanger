package messenger.proj.services;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.User;
import messenger.proj.repositories.ChatRoomRepository;

import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.query.Criteria;
import org.springframework.data.cassandra.core.query.Query;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRep;
	UserService userService;

	@Autowired
	public ChatRoomService(UserService userService, ChatRoomRepository chatRoomRep) {
		this.chatRoomRep = chatRoomRep;
		this.userService = userService;
	}
	
	public Optional<ChatRoom> findById(String chatId) {
		return chatRoomRep.findById(chatId);
	}

	@Transactional
	public void save(String currentUserId, String recipientId) {
		
		Optional<User> currentUser = userService.findById(currentUserId);
		Optional<User> recipient = userService.findById(recipientId);

		Optional<ChatRoom> chatRoom2 = chatRoomRep.findBySenderIdAndRecipientId(recipientId, currentUserId);
		Optional<ChatRoom> chatRoom1 = chatRoomRep.findBySenderIdAndRecipientId(currentUserId, recipientId);
	
		if (!chatRoom2.isPresent() && !chatRoom1.isPresent()) {

			ChatRoom chatRoom = new ChatRoom();

			chatRoom.setId(UUID.randomUUID().toString());
			chatRoom.setSenderId(currentUserId);
			chatRoom.setRecipientId(recipientId);
			chatRoom.setSenderName(currentUser.get().getUsername());
			chatRoom.setRecipientName(recipient.get().getUsername());
			chatRoomRep.save(chatRoom);
		}

	}

	public Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId) {

		Optional<ChatRoom> chatRoom = chatRoomRep.findBySenderIdAndRecipientId(senderId, recipientId);

		return chatRoom;
	}
	
	public List<ChatRoom> findAll(String userId) {
		
		List<ChatRoom> listOne = chatRoomRep.findBySenderId(userId);
		List<ChatRoom> listTwo = chatRoomRep.findByRecipientId(userId);
		
		List<ChatRoom> newList = Stream.concat(listOne.stream(), listTwo.stream()).toList();
		
		return newList;
	}
	
	@Transactional
	public void deleteById(String chatId) {
		chatRoomRep.deleteById(chatId);
	}

}
