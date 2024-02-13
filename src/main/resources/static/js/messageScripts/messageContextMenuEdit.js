$(function() {
	var modal = $("#myModal1");
	var deleteButton = $("#deleteButton1");
	var editButton = $("#editButton1");
	var messageId;
	const editMessageInput = document.getElementById("editMessageInput");

	$("#cachedMessagesContainer").on('contextmenu', function(e) {
		e.preventDefault();

		// Найти ближайший родительский элемент с классом message__wrapper
		var messageWrapper = $(this).closest('.message__wrapper');

		// Получить доступ к элементу с классом message-content внутри message__wrapper
		var messageContent = messageWrapper.find('.message-content');

		// Теперь у вас есть доступ к элементу с классом message-content
		messageId = messageContent.attr("th:id");

		modal.css({ top: e.pageY + "px", left: e.pageX + "px" });
		modal.show();
	});


	$(".close").on("click", function() {
		modal.hide();
	});

	deleteButton.on("click", function() {
		deleteMessage(messageId);
		modal.hide();
	});

	editButton.on("click", function() {

		$("#messageForm").hide();
		$("#editForm").show();
		modal.hide();

		var mess = document.getElementById(messageId);
		
		console.log("MESSAGE " + mess);
		console.log("MESSAGE ID " + messageId);
		
		var sendTime = mess.getAttribute("sendtime");
		var senderName = mess.getAttribute("senderName");
		var chatId = mess.getAttribute("chatId");
		var content = mess.getAttribute("content");
		var senderId = mess.getAttribute("senderId");
		var recipientId = mess.getAttribute("recipientId");
		var status = mess.getAttribute("status");

		editMessageInput.value = content;

		$("#editForm").on("submit", function(event) {
			event.preventDefault(); // Предотвратить стандартное поведение отправки формы

			var editedMessage = $("#editMessageInput").val(); // Получить данные из формы
			var dataToSend = {
				messageId: messageId,
				messageContent: editedMessage
			};


			$.ajax({
				type: "POST",
				url: `/editMessage?messageId=${messageId}&
								chatId=${chatId}&
								sendTime=${sendTime}&
								content=${editedMessage}&
								senderName=${senderName}&
								status=${status}&
								senderId=${senderId}&
								recipientId=${recipientId}`,
				success: function() {
					window.location.href = `/chat/${chatId}`;

				}
			});

			/*	$("#editForm").hide();
				$("#messageForm").show();
				modal.hide();*/
		});


	});

	$(document).on('click', function(e) {
		if (!$(e.target).hasClass("message-content")) {
			modal.hide();
		}
	});
});