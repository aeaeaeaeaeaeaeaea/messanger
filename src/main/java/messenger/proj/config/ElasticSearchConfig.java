package messenger.proj.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

	private final ElasticsearchProperties elasticsearchProperties;

	public ElasticSearchConfig(ElasticsearchProperties elasticsearchProperties) {
		super();
		this.elasticsearchProperties = elasticsearchProperties;
	}

	@Override
	@Bean
	public RestHighLevelClient elasticsearchClient() {
		final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
				.connectedTo("localhost:9200")
				.withConnectTimeout(elasticsearchProperties.getConnectionTimeout())
				.withSocketTimeout(elasticsearchProperties.getSocketTimeout()).build();

		return RestClients.create(clientConfiguration).rest();
	}
	
}
