function showMessage(message) {
	var messageClass = (message.senderId === currentUser ? "my-message" : "other-message");
	$("#chatMessages").append("<p class='" + messageClass + "'>" + message.content + "</p>");
}