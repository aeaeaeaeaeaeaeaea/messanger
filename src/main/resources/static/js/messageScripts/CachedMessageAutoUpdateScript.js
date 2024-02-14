import moment from 'https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.29.1/moment.min.js';

// Функция для обновления данных
function updateCachedMessages() {

	var chatId = $("#userInfoDiv1").attr("data-userid");
	var todayFormat = $("#formatDate").attr("date");
	$.ajax({
		url: '/updateCachedMessages/' + chatId, // URL вашего контроллера
		type: 'GET',
		success: function(data) {
			// Обновление данных на странице
			$('#cachedMessagesContainer').empty();

			$.each(data, function(index, cachedMessage) {

				var sendTime = moment(cachedMessage.sendTime);
				var formattedTime = sendTime.format('HH:mm');

				// Создание HTML-кода для каждого сообщения
				var messageHtml = '<div class="message__wrapper d-flex align-items-end mb-2 mt-2" style="word-break: break-word;">';

				// Вставьте ваш код Thymeleaf для отображения сообщения
				messageHtml += '<div id="divId" class="message-content message__wrapper-box position-relative p-2"';
				messageHtml += 'th:senderName="' + cachedMessage.senderName + '"';
				messageHtml += 'th:content="' + cachedMessage.content + '"';
				messageHtml += 'th:id="' + cachedMessage.id + '"';
				messageHtml += 'th:sendtime="' + cachedMessage.sendTime + '"';
				messageHtml += 'th:chatId="' + cachedMessage.chatId + '"';
				messageHtml += 'th:status="' + cachedMessage.status + '"';
				messageHtml += 'th:recipientId="' + cachedMessage.recipientId + '"';
				messageHtml += 'th:senderId="' + cachedMessage.senderId + '">';

				// Вставьте ваш код Thymeleaf для отображения содержимого файла и других деталей сообщения
				// ...

				messageHtml += '<div class="message__wrapper-top d-flex justify-content-between align-items-center">';
				messageHtml += '<p class="message__user-name" >' + cachedMessage.senderName + '</p>';
				messageHtml += '<span class="message__reply"></span>';
				messageHtml += '</div>';

				messageHtml += '<div th:if="${files.get(cachedMessage.getId())} != null">';

				// Вставьте ваш код Thymeleaf для отображения файлов
				// ...

				messageHtml += '</div>';

				messageHtml += '<div class="message__wrapper-bottom d-flex align-items-end justify-content-between">';
				messageHtml += '<p  th:senderName="${' + cachedMessage.senderName + '}" class="message__wrapper-text">' + cachedMessage.content + '</p>';
				messageHtml += '<div class="message__wrapper-bottom-right d-flex align-items-center">';
				messageHtml += '<time class="message__time" >' + formattedTime + '</time>';
				messageHtml += '<span class="message__status"></span>';
				messageHtml += '</div>';
				messageHtml += '</div>';
				messageHtml += '<span class="message__curve"></span>';

				messageHtml += '</div>';
				messageHtml += '</div>';

				// Добавление HTML-кода в контейнер
				$('#cachedMessagesContainer').append(messageHtml);
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

	setInterval(updateCachedMessages, 1000);
});