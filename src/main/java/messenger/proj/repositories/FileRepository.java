package messenger.proj.repositories;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

import messenger.proj.models.FileEntry;

@Repository
public interface FileRepository extends CassandraRepository<FileEntry, String>{

}
