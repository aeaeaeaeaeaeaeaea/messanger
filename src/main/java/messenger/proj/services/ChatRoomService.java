package messenger.proj.services;


import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import messenger.proj.models.ChatRoom;

import messenger.proj.repositories.ChatRoomRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class ChatRoomService {

	private final ChatRoomRepository chatRoomRep;
	private final UserService userService;
	private final MessageService messageServ;

	@Autowired
	public ChatRoomService(MessageService messageServ, UserService userService, ChatRoomRepository chatRoomRep) {
		this.chatRoomRep = chatRoomRep;
		this.messageServ = messageServ;
		this.userService = userService;
	}

	public Optional<ChatRoom> findById(String chatId) {
		return chatRoomRep.findById(chatId);
	}

	@Transactional
	public void save(ChatRoom chatRoom) {
		if (!findAll().contains(chatRoom)) {
			chatRoom.setId(UUID.randomUUID().toString());
			chatRoomRep.save(chatRoom);
		}
	}

	public Optional<ChatRoom> findBySenderIdAndRecipientId(String senderId, String recipientId) {
		Optional<ChatRoom> chatRoom = chatRoomRep.findBySenderIdAndRecipientId(senderId, recipientId);
		return chatRoom;
	}

	public void readUnreadMessages(ChatRoom chat) {
		chat.setUnreadRecipientMessages(0);
		chat.setUnreadSenderMessages(0);
		chatRoomRep.save(chat);
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

		List<ChatRoom> listOne = chatRoomRep.findBySenderId(userId);
		List<ChatRoom> listTwo = chatRoomRep.findByRecipientId(userId);

		List<ChatRoom> newList = Stream.concat(listOne.stream(), listTwo.stream()).toList();

		Map<String, ChatRoom> chats = new HashMap<>();

		for (ChatRoom chatRoom : newList) {
			chats.put(chatRoom.getId(), chatRoom);
		}

		return chats;
	}

	public List<ChatRoom> findUsersChats(String userId) {
		return Stream.concat(chatRoomRep.findBySenderId(userId).stream(), 
				chatRoomRep.findByRecipientId(userId).stream()).collect(Collectors.toList());
	}

	public List<ChatRoom> findAll() {
		return chatRoomRep.findAll();
	}

	@Transactional
	public void deleteById(String chatId) {
		chatRoomRep.deleteById(chatId);
	}

}
