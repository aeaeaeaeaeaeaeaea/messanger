function deleteMessage(messageId) {

	var mess = document.getElementById(messageId);
	var sendTime = mess.getAttribute("sendtime");
	var chatId = mess.getAttribute("chatId");

	const deleteMessageUrl = `/deleteMessage?messageId=${messageId}&chatId=${chatId}&sendTime=${sendTime}`;


	fetch(deleteMessageUrl, {
		method: 'POST'
	}).then((response) => {
		// Если запрос выполнен успешно, удаляем элемент списка файлов
		if (response.ok) {
			window.location.href = `/chat/${chatId}`;
		}
	});


}

