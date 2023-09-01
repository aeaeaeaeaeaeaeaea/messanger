package messenger.proj.repositories;

import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.ChatRoom;

@Repository
public interface ChatRoomRepository extends CassandraRepository<ChatRoom, String> {
	
	 Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
}
