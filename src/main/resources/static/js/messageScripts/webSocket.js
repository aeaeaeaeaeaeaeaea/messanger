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

	var fileInput = document.getElementById('fileInput');
	var file = fileInput.files[0];

	// Создание объекта FormData и добавление параметров
	var formData = new FormData();
	formData.append("content", message);
	if (file) {
		formData.append("file", file);
	}

	if (message.trim() !== '' && !file) {
		// Отправка сообщения с использованием WebSocket
		stompClient.send("/app/chat/`{chatId}`/sendMessage", {},
			JSON.stringify({
				'content': message,
				'chatId': chatId,
				'dataSenderId': dataSenderId,
				'dataRecipId': dataRecipId
			})
		);
		messageInput.val("");
	}

	// Отправка файла с использованием AJAX
	if (file) {
		$.ajax({
			type: 'POST',
			url: '/upload-file', // Путь к вашему контроллеру для загрузки файла
			headers: { 'chatId': chatId }, // Добавление chatId в заголовки
			data: formData,
			processData: false,
			contentType: false,
			success: function(response) {
				// Обработка успешной отправки файла
			},
			error: function(error) {
				// Обработка ошибки
			}
		});
	}

	

	

	// Очистка полей ввода
	messageInput.val("");
	fileInput.value = ""; // Очистка значения input[type="file"]
}




$(function() {
	$("form#messageForm").on('submit', function(e) {
		e.preventDefault();
		sendMessage();
		window.location.href = `/chat/${chatId}`;
	});
	connectToChat(chatId); // Замените на фактический ID чата
});