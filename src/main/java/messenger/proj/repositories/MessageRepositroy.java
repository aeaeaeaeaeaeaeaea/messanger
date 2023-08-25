package messenger.proj.repositories;



import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.message;

@Repository
public interface MessageRepositroy extends CassandraRepository<message, String> {

}
