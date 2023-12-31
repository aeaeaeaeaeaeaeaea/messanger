package messenger.proj.repositories;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import messenger.proj.models.ElasticUser;
import java.util.Objects;

@Repository
public class ElasticSearchQuery {

	@Autowired
	private ElasticsearchClient elasticsearchClient;
	private final RestHighLevelClient esClient;
	private final String INDEX_NAME = "user";
	
	
	public ElasticSearchQuery(RestHighLevelClient esClient) {
        this.esClient = esClient;
    }
	
	public String createOrUpdateDocument(ElasticUser elasticUser) throws IOException {

		IndexResponse response = elasticsearchClient
				.index(i -> i.index(INDEX_NAME).id(elasticUser.getId()).document(elasticUser));
		if (response.result().name().equals("Created")) {
			return new StringBuilder("Document has been successfully created.").toString();
		} else if (response.result().name().equals("Updated")) {
			return new StringBuilder("Document has been successfully updated.").toString();
		}
		return new StringBuilder("Error while performing the operation.").toString();
	}

//	public ElasticUser getDocumentById(String productId) throws IOException {
//		ElasticUser elasticUser = null;
//		GetResponse<ElasticUser> response = elasticsearchClient.get(g -> g.index(INDEX_NAME).id(productId), ElasticUser.class);
//
//		if (response.found()) {
//			elasticUser = response.source();
//			System.out.println("Product name " + elasticUser.getUserName());
//		} else {
//			System.out.println("Product not found");
//		}
//
//		return elasticUser;
//	}
	
	
	public ArrayList<ElasticUser> search(String userName) throws Exception {
		
		SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchPhraseQuery("userName", userName));
		
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
				
				
		ArrayList<ElasticUser> users = new ArrayList<>();
		
		for (SearchHit hit : searchResponse.getHits().getHits()) {
			
			Map<String, Object> map = hit.getSourceAsMap();
			
			ElasticUser user = new ElasticUser();
			user.setId((String) map.get("id"));
			user.setUserName((String) map.get("userName"));
			users.add(user);
			
		}
		
		
		return users;
	}
	
	


	public String deleteDocumentById(String productId) throws IOException {

		DeleteRequest request = DeleteRequest.of(d -> d.index(INDEX_NAME).id(productId));

		DeleteResponse deleteResponse = elasticsearchClient.delete(request);
		if (Objects.nonNull(deleteResponse.result()) && !deleteResponse.result().name().equals("NotFound")) {
			return new StringBuilder("Product with id " + deleteResponse.id() + " has been deleted.").toString();
		}
		System.out.println("Product not found");
		return new StringBuilder("Product with id " + deleteResponse.id() + " does not exist.").toString();

	}

	public ArrayList<ElasticUser> searchAllDocuments() throws IOException {

		SearchRequest searchRequest = new SearchRequest(INDEX_NAME);
		SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
		
		ArrayList<ElasticUser> users = new ArrayList<>();
		
		for (SearchHit hit : searchResponse.getHits().getHits()) {
			Map<String, Object> map = hit.getSourceAsMap();
			
			ElasticUser user = new ElasticUser();
			
			user.setId((String) map.get("id"));
			user.setUserName((String) map.get("userName"));
			users.add(user);

		}
		
		return users;
	}
	
}
