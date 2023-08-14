package massenger.proj.repositories;

import massenger.proj.models.User;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

    Optional<User> findByUsername(String initials);

}