$(document).ready(function() {
	$(".chat-create").click(function(e) {
		e.preventDefault();

		var mess = document.getElementById('chat');
		var userId = mess.getAttribute("userId");
		var currentUser = mess.getAttribute("currentUser");

		const deleteFileUrl = `/chat?userId=${userId}&currentUser=${currentUser}`;

		fetch(deleteFileUrl, {
			method: 'POST'
		}).then((response) => {
			if (response.ok) {
				// В этом случае response уже содержит строку chatId, не требующую парсинга JSON
				return response.text(); // Возвращаем текст ответа
			}
		}).then((chatId) => {
			window.location.href = `/chat/${chatId}`;
		});

	});
});