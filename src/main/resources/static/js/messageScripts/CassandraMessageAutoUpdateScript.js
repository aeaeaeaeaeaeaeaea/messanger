function updateCassandraMessages() {

	var chatId = $("#userInfoDiv1").attr("data-userid");
	var todayFormat = $("#formatDate").attr("date");
	$.ajax({
		url: '/updateCassandraMessages/' + chatId, // URL вашего контроллера
		type: 'GET',
		success: function(data) {
			// Обновление данных на странице
			$('#cassandraMessagesContainer').empty();

			$.each(data, function(index, cassandraMessages) {

				var sendTime = moment(cassandraMessages.sendTime);
				var formattedTime = sendTime.format('HH:mm');

				// Создание HTML-кода для каждого сообщения
				var messageHtml = '<div class="message__wrapper d-flex align-items-end mb-2 mt-2" style="word-break: break-word;">';

				// Вставьте ваш код Thymeleaf для отображения сообщения
				messageHtml += '<div id="divId" class="message-content message__wrapper-box position-relative p-2"';
				messageHtml += 'th:senderName="' + cassandraMessages.senderName + '"';
				messageHtml += 'th:content="' + cassandraMessages.content + '"';
				messageHtml += 'th:id="' + cassandraMessages.id + '"';
				messageHtml += 'th:sendtime="' + cassandraMessages.sendTime + '"';
				messageHtml += 'th:chatId="' + cassandraMessages.chatId + '"';
				messageHtml += 'th:status="' + cassandraMessages.status + '"';
				messageHtml += 'th:recipientId="' + cassandraMessages.recipientId + '"';
				messageHtml += 'th:senderId="' + cassandraMessages.senderId + '">';

				// Вставьте ваш код Thymeleaf для отображения содержимого файла и других деталей сообщения
				// ...

				messageHtml += '<div class="message__wrapper-top d-flex justify-content-between align-items-center">';
				messageHtml += '<p class="message__user-name" >' + cassandraMessages.senderName + '</p>';
				messageHtml += '<span class="message__reply"></span>';
				messageHtml += '</div>';

				messageHtml += '<div th:if="${files.get(cachedMessage.getId())} != null">';

				// Вставьте ваш код Thymeleaf для отображения файлов
				// ...

				messageHtml += '</div>';

				messageHtml += '<div class="message__wrapper-bottom d-flex align-items-end justify-content-between">';
				messageHtml += '<p  th:senderName="${' + cassandraMessages.senderName + '}" class="message__wrapper-text">' + cassandraMessages.content + '</p>';
				messageHtml += '<div class="message__wrapper-bottom-right d-flex align-items-center">';
				messageHtml += '<time class="message__time" >' + formattedTime + '</time>';
				messageHtml += '<span class="message__status"></span>';
				messageHtml += '</div>';
				messageHtml += '</div>';
				messageHtml += '<span class="message__curve"></span>';

				messageHtml += '</div>';
				messageHtml += '</div>';

				// Добавление HTML-кода в контейнер
				$('#cassandraMessagesContainer').append(messageHtml);
			});

		},
		error: function() {
			console.error('Ошибка при получении данных');
		}
	});
}
// Вызов функции обновления данных при загрузке страницы
$(document).ready(function() {
	updateCachedMessages();

	setInterval(updateCassandraMessages, 1000);
});
