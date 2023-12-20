$(function() {
	var modal = $("#myModal1");
	var deleteButton = $("#deleteButton1");
	var editButton = $("#editButton1");
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
		console.log($("#messageForm"));
		$("#messageForm").hide();
		$("#editForm").show();
		console.log($("#messageForm"));
		modal.hide();

		var mess = document.getElementById(messageId);
		
		var sendTime = mess.getAttribute("sendtime");
		var senderName = mess.getAttribute("senderName");
		var chatId = mess.getAttribute("chatId");
		var content = mess.getAttribute("content");
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
				url: `/editMessage?messageId=${messageId}&chatId=${chatId}&sendTime=${sendTime}&content=${editedMessage}&senderName=${senderName}&status=${status}`,
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