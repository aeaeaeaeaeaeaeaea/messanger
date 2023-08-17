package messenger.proj.repositories;

import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.Chat;


@Repository
public interface ChatRepository extends CassandraRepository<Chat, UUID> {

}
