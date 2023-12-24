package messenger.proj.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.FileEntry;
import messenger.proj.repositories.FileRepository;

@Service
@Transactional(readOnly = true)
public class FileService {
	
	private FileRepository fileRepository;
	
	public FileService(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}
	
	@Transactional
	public void save(FileEntry file) {
		fileRepository.save(file);
	}
	
	
  
}
