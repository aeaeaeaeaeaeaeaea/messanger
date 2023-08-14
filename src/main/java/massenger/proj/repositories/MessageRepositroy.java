package massenger.proj.repositories;


import java.util.UUID;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import massenger.proj.models.message;

@Repository
public interface MessageRepositroy extends CassandraRepository<message, UUID> {

}
