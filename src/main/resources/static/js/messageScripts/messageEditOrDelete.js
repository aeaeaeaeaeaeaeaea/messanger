function deleteMessage(messageId, sendTime, chatId) {

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

