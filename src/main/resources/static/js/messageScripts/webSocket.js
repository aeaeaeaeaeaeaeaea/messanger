var stompClient = null;
var mess = document.getElementById('messageInput');
var chatId = mess.getAttribute("data");
var dataSenderId = mess.getAttribute("dataSenderId");
var dataRecipId = mess.getAttribute("dataRecipId");

function connectToChat(chatId) {
	var socket = new SockJS('/chat/' + chatId);
	stompClient = Stomp.over(socket);
	stompClient.connect({}, function(frame) {
		console.log('Connected: ' + frame);

		
	});
}




function sendMessage() {
	var messageInput = $("#messageInput");
	var message = messageInput.val();

	var fileInput = document.getElementById('fileInput');
	var file = fileInput.files[0];

	if (message.trim() !== '' || file) {
		stompClient.send(`/app/chat/${chatId}/sendMessage`, {},
			JSON.stringify({
				'content': message,
				'chatId': chatId,
				'dataSenderId': dataSenderId,
				'dataRecipId': dataRecipId
			})
		);

		// Очистка полей ввода
		messageInput.val("");
		fileInput.value = ""; // Очистка значения input[type="file"]
	}
}

$(function() {
	$("form#messageForm").on('submit', function(e) {
		e.preventDefault();
		sendMessage();
	});

	connectToChat(chatId);
});
