$(function() {
	var modal = $("#myModal");
	var deleteButton = $("#deleteButton");
	var editButton = $("#editButton");
	var messageId;

	$(".message-content").on('contextmenu', function(e) {
		e.preventDefault();
		messageId = $(this).attr("data-id");
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
		var messageContent = $(".message-content[data-id='" + messageId + "']").text();
		$("#editedContent").val(messageContent);
		$("#editChatId").val(chatId);
		$("#editMessageId").val(messageId);

		$("#editForm").show();
		$("#messageForm").hide();
		modal.hide();
	});

	$(document).on('click', function(e) {
		if (!$(e.target).hasClass("message-content")) {
			modal.hide();
		}
	});
});