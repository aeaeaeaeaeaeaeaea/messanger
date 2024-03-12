package messenger.proj.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.proj.models.ElasticUser;
import messenger.proj.models.User;
import messenger.proj.repositories.ElasticSearchQuery;
import messenger.proj.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private UserRepository userRep;
	private ElasticSearchQuery elasticSearchQuery;

	@Autowired
	public UserService(UserRepository userRep, ElasticSearchQuery elasticSearchQuery) {
		this.userRep = userRep;
		this.elasticSearchQuery = elasticSearchQuery;
	}

	public void editUser(User user) {
		userRep.save(user);
	}

	@Transactional
	public void save(User user) {
		String id = UUID.randomUUID().toString();
		user.setId(id);
		user.setRole("ROLE_USER");
		userRep.save(user);

		ElasticUser elasticUser = new ElasticUser(user.getId(), user.getUsername());

		try {
			elasticSearchQuery.createOrUpdateDocument(elasticUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<User> findAll() {
		return userRep.findAll();
	}

	public Optional<User> findById(String id) {
		return userRep.findById(id);
	}

	

	

}
