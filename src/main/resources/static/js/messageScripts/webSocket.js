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
		stompClient.subscribe('/topic/' + chatId, function(message) {
			showMessage(JSON.parse(message.body));
		});
	});
}

function showMessage(message) {
	$("#chatMessages").append("<p>" + message.content + "</p>");
}

function sendMessage() {
	var messageInput = $("#messageInput");
	var message = messageInput.val();
	stompClient.send("/app/chat/`{chatId}`/sendMessage", {}, JSON.stringify({'chatId': chatId, 'dataSenderId': dataSenderId, 'dataRecipId': dataRecipId}));
	messageInput.val("");
}

$(function() {
	$("form#messageForm").on('submit', function(e) {
		e.preventDefault();
		sendMessage();
	});
	connectToChat(chatId); // Замените на фактический ID чата
});