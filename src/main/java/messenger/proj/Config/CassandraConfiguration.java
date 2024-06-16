package messenger.proj.Config;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories
public class CassandraConfiguration extends AbstractCassandraConfiguration {

	@Override
	public String getContactPoints() {
		return "cassandra"; // Should match the service name in docker-compose.yml
	}

	@Override
	protected String getKeyspaceName() {
		return "k1";
	}

	@Override
	protected int getPort() {
		return 9042;
	}

	@Override
	protected String getLocalDataCenter() {
		return "datacenter1";
	}
}
