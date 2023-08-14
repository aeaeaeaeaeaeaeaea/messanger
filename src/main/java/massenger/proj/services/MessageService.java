package massenger.proj.services;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import massenger.proj.models.message;
import massenger.proj.repositories.MessageRepositroy;

@Service
@Transactional(readOnly = true)
public class MessageService {
	
	private final MessageRepositroy messageRep;
	
	@Autowired
	public MessageService(MessageRepositroy messageRep) {
		this.messageRep = messageRep;
	}
	
	public List<message> findAll() {
		return messageRep.findAll();
	}
	
	@Transactional
	public void save(message message) {
		
		UUID id = UUID.randomUUID();
		
		message.setId(id);
		
		messageRep.save(message);
	}
}
