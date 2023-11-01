$(function() {
	var modal = $("#myModal");
	var deleteButton = $("#deleteButton");
	var editButton = $("#editButton");
	var messageId;
	const editMessageInput = document.getElementById("editMessageInput");

	$(".message-content").on('contextmenu', function(e) {
		e.preventDefault();
		messageId = $(this).attr("id");
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

		$("#editForm").show();
		$("#messageForm").hide();
		modal.hide();

		var mess = document.getElementById(messageId);
		var sendTime = mess.getAttribute("sendtime");
		var chatId = mess.getAttribute("chatId");
		var content = mess.getAttribute("content");
		
		console.log("Content " + content);

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
				url: `/editMessage?messageId=${messageId}&chatId=${chatId}&sendTime=${sendTime}&content=${editedMessage}`,
				success: function() {
					window.location.href = `/chat/${chatId}`;

				}
			});

			$("#editForm").hide();
			$("#messageForm").show();
			modal.hide();
		});


	});

	$(document).on('click', function(e) {
		if (!$(e.target).hasClass("message-content")) {
			modal.hide();
		}
	});
});