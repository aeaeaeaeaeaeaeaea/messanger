package messenger.proj.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.User;
import messenger.proj.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private UserRepository userRep;

	@Autowired
	public UserService(UserRepository userRep) {
		this.userRep = userRep;
	}

	@Transactional
	public void save(User user) {

		UUID id = UUID.randomUUID();

		user.setId(id);
		
		user.setRole("ROLE_USER");
		
		userRep.save(user);
	}
	
	public List<User> findAll() {
		return userRep.findAll();
	}

}
