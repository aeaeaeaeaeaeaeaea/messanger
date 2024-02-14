$(function() {
	var modal = $("#myModal1");
	var deleteButton = $("#deleteButton1");
	var editButton = $("#editButton1");
	var messageId;
	var sendTime;
	var senderName;
	var chatId;
	var content;
	var senderId;
	var recipientId;
	var status;
	const editMessageInput = document.getElementById("editMessageInput");

	$('#cachedMessagesContainer').on('contextmenu', '#divId', function(e) {
		e.preventDefault();
		messageId = $(this).attr("th:id");

		sendTime = $(this).attr("th:sendtime");
		senderName = $(this).attr("th:senderName");
		chatId = $(this).attr("th:chatId");
		content = $(this).attr("th:content");
		senderId = $(this).attr("th:senderId");
		recipientId = $(this).attr("th:recipientId");
		status = $(this).attr("th:status");

		modal.css({ top: e.pageY + "px", left: e.pageX + "px" });
		modal.show();
	});


	$(".close").on("click", function() {
		modal.hide();
	});

	deleteButton.on("click", function() {
		deleteMessage(messageId, sendTime, chatId);
		modal.hide();
	});

	editButton.on("click", function() {

		$("#messageForm").hide();
		$("#editForm").show();
		modal.hide();

		var mess = document.getElementById(messageId);

	



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