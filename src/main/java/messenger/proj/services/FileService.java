package messenger.proj.services;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.datastax.oss.driver.shaded.guava.common.io.Files;

import messenger.proj.models.FileEntry;
import messenger.proj.repositories.FileRepository;

import java.io.File;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	public Map<String, FileEntry> getFiles() {
		
		Map<String, FileEntry> filesMap = new HashMap<>();
		for (FileEntry file : fileRepository.findAll()) {
			filesMap.put(file.getMessageId(), file);
		}
		
		return filesMap;
 	}

	public void saveFileToServer(MultipartFile file, String uploadDir) throws IOException {
		
		byte[] bytes = file.getBytes();

        // Создаем путь для сохранения файла
        String filePath = uploadDir + File.separator + file.getOriginalFilename();
        File newFile = new File(filePath);

        // Записываем байты в файл
        file.transferTo(newFile);
    }
	
	
  
}
