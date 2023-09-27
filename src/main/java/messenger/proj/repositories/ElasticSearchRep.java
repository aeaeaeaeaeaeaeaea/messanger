package messenger.proj.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.ElasticUser;
import messenger.proj.models.User;

@Repository
public interface ElasticSearchRep extends ElasticsearchRepository<ElasticUser, String> 
{
	
	List<User> findByUserName(String userName);
	
}
