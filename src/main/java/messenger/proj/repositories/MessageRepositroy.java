package messenger.proj.repositories;



import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import messenger.proj.models.User;
import messenger.proj.models.message;

@Repository
public interface MessageRepositroy extends CassandraRepository<message, String> {
	
	@Query("SELECT * FROM chatmessage WHERE recipientid = ?0 AND senderid = ?1 ALLOW FILTERING")
	List<message> findByChatId(String chatId, String senderId);
}
