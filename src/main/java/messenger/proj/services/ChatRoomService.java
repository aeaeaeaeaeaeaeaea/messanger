package messenger.proj.services;

import java.util.UUID;
import java.util.stream.Stream;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.proj.models.ChatRoom;
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
	private final CassandraTemplate cassandraTemplate;

	@Autowired
	public ChatRoomService(ChatRoomRepository chatRoomRep, CassandraTemplate cassandraTemplate) {
		this.chatRoomRep = chatRoomRep;
		this.cassandraTemplate = cassandraTemplate;
	}

	@Transactional
	public void save(String senderId, String recipientId) {

		
	
		Optional<ChatRoom> chatRoom2 = chatRoomRep.findBySenderIdAndRecipientId(recipientId, senderId);
		Optional<ChatRoom> chatRoom1 = chatRoomRep.findBySenderIdAndRecipientId(senderId, recipientId);

		if (!chatRoom2.isPresent() && !chatRoom1.isPresent()) {

			ChatRoom chatRoom = new ChatRoom();

			chatRoom.setId(UUID.randomUUID().toString());
			chatRoom.setSenderId(senderId);
			chatRoom.setRecipientId(recipientId);
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

}
