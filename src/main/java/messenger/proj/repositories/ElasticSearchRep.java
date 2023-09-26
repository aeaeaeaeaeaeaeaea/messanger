package messenger.proj.repositories;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.User;

@Repository
public interface ElasticSearchRep extends ElasticsearchRepository<User, String> {
	
	List<User> findByFieldName(String fieldName);
	
}
