package messenger.proj.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import messenger.proj.models.User;
import messenger.proj.repositories.ElasticSearchRep;
import messenger.proj.repositories.UserRepository;

@Service
public class ElasitSearchSerivce {

	private final ElasticSearchRep userRep;

	@Autowired
	public ElasitSearchSerivce(ElasticSearchRep userRep) {
		super();
		this.userRep = userRep;
	}
	

	public List<User> searchByFieldName(String fieldName) {
		return userRep.findByFieldName(fieldName);
	}

	

}
