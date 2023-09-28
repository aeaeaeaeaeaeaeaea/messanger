package messenger.proj.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import messenger.proj.models.ElasticUser;
import messenger.proj.models.User;
import messenger.proj.repositories.ElasticSearchRep;
import messenger.proj.repositories.UserRepository;

@Service
public class ElasitSearchSerivce {

	private final ElasticSearchRep elasticSearchRep;

	@Autowired
	public ElasitSearchSerivce(ElasticSearchRep elasticSearchRep) {
		super();
		this.elasticSearchRep = elasticSearchRep;
	}
	
	public void save(ElasticUser elasticUser) {
		elasticSearchRep.save(elasticUser);
	}

	public List<ElasticUser> searchByFieldName(String userName) {
		return elasticSearchRep.findByUserName(userName);
	}

}
