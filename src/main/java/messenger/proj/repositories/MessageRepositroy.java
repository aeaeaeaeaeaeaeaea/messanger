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
	
	@Query("SELECT * FROM messages WHERE chatId = ?0 ORDER BY sendtime")
	List<message> findByChatId(String chatId);
	
	@Query("DELETE FROM messages WHERE chatid = ?0 IF EXISTS")
	void deleteByChatId(String chatId);
	
}
