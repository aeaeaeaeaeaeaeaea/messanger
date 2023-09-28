package messenger.proj.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import messenger.proj.models.ChatRoom;

@Repository
public interface ChatRoomRepository extends CassandraRepository<ChatRoom, String> {

	@Query("SELECT * FROM chatroom WHERE recipientid = ?0 AND senderid = ?1 ALLOW FILTERING")
	Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId);
	
	@Query("SELECT * FROM chatroom WHERE senderid = ?0 ALLOW FILTERING")
	List<ChatRoom> findBySenderId(String senderId);
	
	
	@Query("SELECT * FROM chatroom WHERE recipientid = ?0 ALLOW FILTERING")
	List<ChatRoom> findByRecipientId(String recipientId);

}
