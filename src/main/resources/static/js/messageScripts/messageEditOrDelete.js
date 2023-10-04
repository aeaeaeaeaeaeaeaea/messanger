function deleteMessage(messageId) {

			var mess = document.getElementById('messageInput');
			var chatId = mess.getAttribute("data");

			const deleteMessageUrl = `/deleteMessage?messageId=${messageId}&chatId=${chatId}`;


			fetch(deleteMessageUrl, {
				method: 'POST'
			}).then((response) => {
				// Если запрос выполнен успешно, удаляем элемент списка файлов
				if (response.ok) {
					window.location.href = `/chat/${chatId}`;
				}
			});


		}

		function editMessage(messageId) {

			var mess = document.getElementById('messageInput');
			var chatId = mess.getAttribute("data");

			$.ajax({
				type: "POST",
				url: "/editMessage?messageId=" + messageId, // Замените на путь к вашему @PostMapping
				success: function () {
					console.log("Редактирование сообщения");
					// Откройте модальное окно с формой редактирования или выполните другие действия
				}
			});
		}