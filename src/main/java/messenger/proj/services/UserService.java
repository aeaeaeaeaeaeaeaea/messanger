package messenger.proj.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.ElasticUser;
import messenger.proj.models.User;
import messenger.proj.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private UserRepository userRep;
	private ElasitSearchSerivce elasitSearchServ;
	
	@Autowired
	public UserService(UserRepository userRep, ElasitSearchSerivce elasitSearchServ) {
		this.userRep = userRep;
		this.elasitSearchServ = elasitSearchServ;
	}

	@Transactional
	public void save(User user) {
		String id = UUID.randomUUID().toString();
		user.setId(id);	
		user.setRole("ROLE_USER");
		userRep.save(user);
		
		ElasticUser elasticUser = new ElasticUser(user.getId(), user.getUsername());
		elasitSearchServ.save(elasticUser);
	}
	
	public List<User> findAll() {
		return userRep.findAll();
	}
	
	public Optional<User> findById(String id) {
		return userRep.findById(id);
	}
 
}
