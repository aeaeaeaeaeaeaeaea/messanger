document.addEventListener("DOMContentLoaded", function() {
	// Найдем контейнер сообщений
	var messageContainer = document.getElementById("message-container");

	// Прокрутим его вниз
	messageContainer.scrollTop = messageContainer.scrollHeight;
});

