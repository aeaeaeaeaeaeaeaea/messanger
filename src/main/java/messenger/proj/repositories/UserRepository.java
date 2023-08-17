package messenger.proj.repositories;

import messenger.proj.models.User;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends CassandraRepository<User, UUID> {

	@Query("SELECT * FROM users WHERE username=?0 ALLOW FILTERING")
	Optional<User> findByUsername(String username);


}