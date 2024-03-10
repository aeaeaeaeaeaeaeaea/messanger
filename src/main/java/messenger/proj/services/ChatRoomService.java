package messenger.proj.services;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import messenger.proj.DTO.ChatRoomDTO;
import messenger.proj.models.ChatRoom;
import messenger.proj.models.User;
import messenger.proj.repositories.ChatRoomRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRepository;
	private final UserService userService;
	private final ModelMapper modelMapper;

	
	@Autowired
	public ChatRoomService(UserService userService, 
						ModelMapper modelMapper,
			ChatRoomRepository chatRoomRepository) {
		this.chatRoomRepository = chatRoomRepository;
		this.modelMapper = modelMapper;
		this.userService = userService;
	}
	
	@Transactional
	public void edit(ChatRoom chatRoom) {
		chatRoomRepository.save(chatRoom);
	}

	public Optional<ChatRoom> findById(String chatId) {
		return chatRoomRepository.findById(chatId);
	}
	
	public ChatRoom convertToChatRoom(ChatRoomDTO chatRoomDTO) {
		return modelMapper.map(chatRoomDTO, ChatRoom.class);
	}

	@Transactional
	public void save(ChatRoomDTO chatRoom) {
		if (!findAll().contains(convertToChatRoom(chatRoom))) {
			chatRoom.setId(UUID.randomUUID().toString());
			
			Optional<User> recipientUser = userService.findById(chatRoom.getRecipientId());
			Optional<User> sernderUser = userService.findById(chatRoom.getSenderId());
			
			if (recipientUser.isPresent()) {
				chatRoom.setRecipientName(recipientUser.get().getUsername());
			}
			
			if (sernderUser.isPresent()) {
				chatRoom.setSenderName(sernderUser.get().getUsername());
			}
 			
			
			chatRoomRepository.save(convertToChatRoom(chatRoom));
		}
	}

	public Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId) {
		Optional<ChatRoom> chatRoom = chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId);
		return chatRoom;
	}

	

	/*
	 * public List<ChatRoom> lastMessageOrder(String curentUserId) {
	 * 
	 * List<ChatRoom> chatRooms = new ArrayList<>(); Map<String, ChatRoom> map =
	 * findAllHashMap(curentUserId);
	 * 
	 * for (Map.Entry<String, message> mEntry :
	 * messageServ.getLastMessage(findAll(curentUserId)).entrySet()) {
	 * 
	 * chatRooms.add(map.get(mEntry.getKey()));
	 * 
	 * }
	 * 
	 * for (Map.Entry<String, ChatRoom> mEntry : map.entrySet()) { if
	 * (!chatRooms.contains(map.get(mEntry.getKey()))) {
	 * chatRooms.add(mEntry.getValue()); } }
	 * 
	 * return chatRooms; }
	 */

	public Map<String, ChatRoom> findAllHashMap(String userId) {

		List<ChatRoom> listOne = chatRoomRepository.findBySenderId(userId);
		List<ChatRoom> listTwo = chatRoomRepository.findByRecipientId(userId);

		List<ChatRoom> newList = Stream.concat(listOne.stream(), listTwo.stream()).toList();

		Map<String, ChatRoom> chats = new HashMap<>();

		for (ChatRoom chatRoom : newList) {
			chats.put(chatRoom.getId(), chatRoom);
		}

		return chats;
	}

	public List<ChatRoom> findUsersChats(String userId) {
		return Stream
				.concat(chatRoomRepository.findBySenderId(userId).stream(), chatRoomRepository.findByRecipientId(userId).stream())
				.collect(Collectors.toList());
	}

	public List<ChatRoom> findAll() {
		return chatRoomRepository.findAll();
	}

	@Transactional
	public void deleteById(String chatId) {
		chatRoomRepository.deleteById(chatId);
	}

	/*
	 * // Метод, который считает сообщения со статусом 'Unread' public void
	 * countUnreadMessages(String chatId) {
	 * 
	 * Optional<ChatRoom> chat = findById(chatId);
	 * 
	 * if (chat.isPresent()) {
	 * 
	 * for (message message : messageService.getCaсhedMessages(chatId)) {
	 * 
	 * 
	 * 
	 * }
	 * 
	 * for (message message : messageService.getCassandraMessages(chatId)) {
	 * 
	 * if (message != null && message.getStatus().equals("Unread") &&
	 * message.getSenderId().equals(chat.get().getSenderId())) {
	 * 
	 * // Увеличиваем счетчик для unreadRecipientMessages
	 * chat.get().setUnreadRecipientMessages(chat.get().getUnreadRecipientMessages()
	 * + 1); save(chat.get());
	 * 
	 * } else if (message.getStatus().equals("Unread") &&
	 * message.getRecipientId().equals(chat.get().getSenderId())) {
	 * 
	 * // Увеличиваем счетчик для unreadSenderMessages
	 * chat.get().setUnreadSenderMessages(chat.get().getUnreadSenderMessages() + 1);
	 * save(chat.get());
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * }
	 */

}
