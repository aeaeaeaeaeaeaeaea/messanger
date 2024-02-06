package messenger.proj.services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.models.ElasticUser;
import messenger.proj.models.User;
import messenger.proj.repositories.ElasticSearchQuery;
import messenger.proj.repositories.UserRepository;

@Service
@Transactional(readOnly = true)
public class UserService {

	private UserRepository userRep;
	private ElasticSearchQuery elasticSearchQuery;

	@Autowired
	public UserService(UserRepository userRep, ElasticSearchQuery elasticSearchQuery) {
		this.userRep = userRep;
		this.elasticSearchQuery = elasticSearchQuery;
	}

	public void editUser(User user) {
		userRep.save(user);
	}

	@Transactional
	public void save(User user) {
		String id = UUID.randomUUID().toString();
		user.setId(id);
		user.setRole("ROLE_USER");
		userRep.save(user);

		ElasticUser elasticUser = new ElasticUser(user.getId(), user.getUsername());

		try {
			elasticSearchQuery.createOrUpdateDocument(elasticUser);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<User> findAll() {
		return userRep.findAll();
	}

	public Optional<User> findById(String id) {
		return userRep.findById(id);
	}

	public String getCurrentUserAvatar(User currentUser) {

		ByteBuffer currentUserimageByteBuffer = currentUser.getAvatar();
		String currentUserAvatar = "";
		
		if (currentUserimageByteBuffer != null) {

			ByteBuffer currentUserduplicateBuffer = currentUserimageByteBuffer.duplicate();
			// Преобразуем ByteBuffer в массив байт
			byte[] imageBytes = new byte[currentUserduplicateBuffer.remaining()];
			currentUserduplicateBuffer.get(imageBytes);

			// Кодируем массив байт в строку Base64
			currentUserAvatar = Base64.getEncoder().encodeToString(imageBytes);

			// Передаем строку Base64 в представление через modal.addAttribute

		}
		
		return currentUserAvatar;

	}

	public String getRecipientUserAvatar(User recipientUser) {

		ByteBuffer recipientUserimageByteBuffer = recipientUser.getAvatar();
		String recipientUserAvatar = "";
		
		
		if (recipientUserimageByteBuffer != null) {

			ByteBuffer recipientUserduplicateBuffer = recipientUserimageByteBuffer.duplicate();
			// Преобразуем ByteBuffer в массив байт
			byte[] imageBytes = new byte[recipientUserduplicateBuffer.remaining()];
			recipientUserduplicateBuffer.get(imageBytes);

			// Кодируем массив байт в строку Base64
			recipientUserAvatar = Base64.getEncoder().encodeToString(imageBytes);

			// Передаем строку Base64 в представление через modal.addAttribute

		}
		
		return recipientUserAvatar;

	}

}
