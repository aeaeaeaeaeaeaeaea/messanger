package messenger.proj.repositories;


import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.message;

@Repository
public interface MessageRepositroy extends CassandraRepository<message, UUID> {

}
