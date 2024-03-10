package messenger.proj.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.elasticsearch.cluster.coordination.Publication;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jnr.ffi.Struct.int16_t;
import messenger.proj.DTO.MessageDTO;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.ConnectionInfo;
import messenger.proj.models.Message;
import messenger.proj.repositories.ChatRoomRepository;
import messenger.proj.repositories.MessageRepositroy;

@Service
@Transactional(readOnly = true)
public class MessageService {

	private final MessageRepositroy messageRepositroy;
	private final MessageRedisService messageRedisService;
	private final RedisTemplate<String, Message> redisTemplate;
	private final RedisTemplate<String, String> redisTemplate1;
	private final ConnectionService connectionService;
	private final ChatRoomService chatRoomService;
	private final ModelMapper modelMapper;

	@Autowired
	public MessageService(MessageRepositroy messageRepositroy, 
			ModelMapper modelMapper,
			ChatRoomService chatRoomService,
			ConnectionService connectionService, MessageRedisService messageRedisService,
			RedisTemplate<String, Message> redisTemplate, RedisTemplate<String, String> redisTemplate1) {

		this.chatRoomService = chatRoomService;
		this.modelMapper = modelMapper;
		this.messageRepositroy = messageRepositroy;
		this.connectionService = connectionService;
		this.redisTemplate1 = redisTemplate1;
		this.redisTemplate = redisTemplate;
		this.messageRedisService = messageRedisService;
	}

	public List<Message> findAll() {
		return messageRepositroy.findAll();
	}

	public Optional<Message> findById(String messageId) {
		return messageRepositroy.findById(messageId);
	}
	
	public Message convertToMessage(MessageDTO messageDTO) {
		return modelMapper.map(messageDTO, Message.class);
	}
	
	public MessageDTO convertToDto(Message message) {
		return modelMapper.map(message, MessageDTO.class);
	}

	@Transactional
	public void save(MessageDTO messageDTO, String chatId, String currentUserId) {

		String id = UUID.randomUUID().toString();
		Optional<ChatRoom> chat = chatRoomService.findById(chatId);
		
		if (chat.isPresent()) {
			
			Message message = convertToMessage(messageDTO);
			
			message.setId(id);
			message.setChatId(chatId);

			if (chat.get().getSenderId().equals(currentUserId)) {

				message.setSenderId(currentUserId);
				message.setSenderName(chat.get().getSenderName());
				message.setRecipientId(chat.get().getRecipientId());
				message.setRecipientName(chat.get().getRecipientName());

			} else {

				message.setSenderId(chat.get().getRecipientId());
				message.setSenderName(chat.get().getRecipientName());
				message.setRecipientId(chat.get().getSenderId());
				message.setRecipientName(chat.get().getSenderName());

			}
			
			message.setStatus("Unread");
			message.setSendTime(LocalDateTime.now());

			messageRedisService.cacheMessage(id, chatId, message);

			increaseUnreadMessageCounter(message);
		}

	}

	@Transactional
	public void increaseUnreadMessageCounter(Message message) {

		Optional<ChatRoom> chat = chatRoomService.findById(message.getChatId());

		if (chat.isPresent()) {

			if (message != null && message.getStatus().equals("Unread")
					&& message.getSenderId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadRecipientMessages
				chat.get().setUnreadRecipientMessages(chat.get().getUnreadRecipientMessages() + 1);
				chatRoomService.edit(chat.get());

			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadSenderMessages
				chat.get().setUnreadSenderMessages(chat.get().getUnreadSenderMessages() + 1);
				chatRoomService.edit(chat.get());

			}

		}

	}

	@Transactional
	public void decreaseUnreadMessageCounter(Message message) {

		Optional<ChatRoom> chat = chatRoomService.findById(message.getChatId());

		if (chat.isPresent()) {

			if (message != null && message.getStatus().equals("Unread")
					&& message.getSenderId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadRecipientMessages
				chat.get().setUnreadRecipientMessages(chat.get().getUnreadRecipientMessages() - 1);
				chatRoomService.edit(chat.get());

			} else if (message.getStatus().equals("Unread")
					&& message.getRecipientId().equals(chat.get().getSenderId())) {

				// Увеличиваем счетчик для unreadSenderMessages
				chat.get().setUnreadSenderMessages(chat.get().getUnreadSenderMessages() - 1);
				chatRoomService.edit(chat.get());

			}

		}

	}

	public List<Message> findByChatId(String chatId) {
		return Stream.concat(getCassandraMessages(chatId).stream(), getCaсhedMessages(chatId).stream())
				.collect(Collectors.toList());
	}
	
	public List<MessageDTO> findByChatIdDTO(String chatId) {
		return Stream.concat(
				getCassandraMessages(chatId).stream().map(x -> convertToDto(x)), 
				getCaсhedMessages(chatId).stream().map(x -> convertToDto(x)))
				.collect(Collectors.toList());
	}

	@Transactional
	public void deleteById(MessageDTO messageForData) {

		Optional<Message> messageOptional = findById(messageForData.getId());
		
		if (!messageOptional.isPresent()) {
			Message message = messageRedisService.getMessageById(messageForData.getId(), messageForData.getChatId());
			
			messageRepositroy.deleteById(message.getId());
			redisTemplate.delete("message:" + message.getChatId() + ":" + message.getId());
			redisTemplate1.opsForList().remove(message.getChatId(), 0,
					"message:" + message.getChatId() + ":" + message.getId());

			decreaseUnreadMessageCounter(message);
			
		}
		
		
		if (messageOptional.isPresent()) {
			
			Message message = messageOptional.get();
			
			messageRepositroy.deleteById(message.getId());
			redisTemplate.delete("message:" + message.getChatId() + ":" + message.getId());
			redisTemplate1.opsForList().remove(message.getChatId(), 0,
					"message:" + message.getChatId() + ":" + message.getId());

			decreaseUnreadMessageCounter(message);

		}

	}

	public void deleteAllMessagesFromChat(String chatId) {
		findByChatId(chatId).forEach(x -> deleteById(convertToDto(x)));
		getCaсhedMessages(chatId).forEach(x -> deleteById(convertToDto(x)));

		Optional<ChatRoom> chat = chatRoomService.findById(chatId);

		if (chat.isPresent()) {
			chat.get().setUnreadRecipientMessages(0);
			chat.get().setUnreadSenderMessages(0);
			chatRoomService.edit(chat.get());
		}

	}

	public void readMessages(String curentUserId, ChatRoom chat, HttpServletRequest request) {

		boolean flag = false;

		List<Message> messages = findByChatId(chat.getId());
		int end = messages.size() - 1;

		while (end >= 0 && messages.get(end).getStatus().equals("Unread")) {

			if (messages.get(end).getRecipientId().equals(curentUserId)) {

				messages.get(end).setStatus("Read");
				edit(convertToDto(messages.get(end)));
				flag = true;

				if (chat.getRecipientId().equals(curentUserId)) {
					chat.setUnreadRecipientMessages(0);
					chatRoomService.edit(chat);
				} else {
					chat.setUnreadSenderMessages(0);
					chatRoomService.edit(chat);
				}

			}

			end--;
		}

		if (flag) {
			if (chat.getRecipientId().equals(curentUserId)) {
				connectionService.userConnection(curentUserId, connectionService.getUserConnection(curentUserId),
						chat.getSenderId());
			} else {
				connectionService.userConnection(curentUserId, connectionService.getUserConnection(chat.getSenderId()),
						chat.getRecipientId());
			}

		}

	}
	
	//Остановился тут
	@Transactional
	public void edit(MessageDTO message) {
		List<Message> messages = getCaсhedMessages(message.getChatId());
		if (messages.contains(convertToMessage(message))) {
			redisTemplate.opsForValue().set("message:" + message.getChatId() + ":" + message.getId(), convertToMessage(message));
		} else {
			messageRepositroy.save(convertToMessage(message));
		}
	}

	public List<Message> getCaсhedMessages(String chatId) {
		return messageRedisService.getLatestMessages(chatId);
	}

	public List<Message> getCassandraMessages(String chatId) {
		return messageRepositroy.findByChatId(chatId);
	}

	public Map<String, Message> getLastMessage(List<ChatRoom> chats) {

		HashMap<String, Message> lastMessages = new HashMap<>();

		for (ChatRoom chatRoom : chats) {
			try {
				// Получаю последние кэшированные сообщения
				List<Message> messages = getCaсhedMessages(chatRoom.getId());
				// Если последних кэшированных сообщений нет, то получаем сообщения из cassandra
				if (messages.isEmpty()) {
					List<Message> cassandraMessages = getCassandraMessages(chatRoom.getId());
					lastMessages.put(chatRoom.getId(), cassandraMessages.get(cassandraMessages.size() - 1));
					// Кладем сообщения в hashMap для дальнейшего использования из redis'а, если они
					// есть
				} else {
					lastMessages.put(chatRoom.getId(), messages.get(messages.size() - 1));
				}

			} catch (IndexOutOfBoundsException e) {

			}
		}

		if (lastMessages.size() != 0) {

			List<Map.Entry<String, Message>> list = new ArrayList<>(lastMessages.entrySet());

			Collections.sort(list, Comparator.comparing(entry -> entry.getValue().getSendTime()));
			Collections.reverse(list);

			Map<String, Message> sortedHashMap = new LinkedHashMap<>();

			for (Map.Entry<String, Message> entry : list) {
				sortedHashMap.put(entry.getKey(), entry.getValue());
			}

			return sortedHashMap;
		}

		return lastMessages;

	}

}
