package messenger.proj.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import messenger.proj.models.Message;
import messenger.proj.models.User;

@Repository
public interface MessageRepositroy extends CassandraRepository<Message, String> {

	@Query("SELECT * FROM testmessages WHERE chatId = ?0 ALLOW FILTERING")
	List<Message> findByChatId(String chatId);
	
	/*
	 * @Query("SELECT * FROM message WHERE chatId = ?0 AND sendtime = ?1 AND id = ?2"
	 * ) List<message> findMessage(String chatId, LocalDateTime localDateTime,
	 * String id);
	 */

	/*
	 * @Query("DELETE FROM message WHERE  id = ?1") void deleteByChatId(String id);
	 */

}
