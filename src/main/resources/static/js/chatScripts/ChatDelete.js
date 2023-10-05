$(function() {
	var modal = $("#myModal");
	var deleteButton = $("#deleteButton");
	var editButton = $("#editButton");
	var chatId;


	$(".chat-link").on('contextmenu', function(e) {
		e.preventDefault();
		chatId = $(this).text();
		modal.css({ top: e.pageY + "px", left: e.pageX + "px" });
		modal.show();
	});


	$(".close").on("click", function() {
		modal.hide();
	});


	deleteButton.on("click", function() {
		deleteChat(chatId);
		modal.hide();
	});


	editButton.on("click", function() {

		redirectToEditPage(chatId);
		modal.hide();
	});


	$(document).on('click', function(e) {
		if (!$(e.target).hasClass("list-group-item list-group-item-action")) {
			modal.hide();
		}
	});
});


function deleteChat(chatId) {

	const deleteFileUrl = `/deleteChat?chatId=${chatId}`;


	fetch(deleteFileUrl, {
		method: 'POST'
	}).then((response) => {

		if (response.ok) {
			window.location.href = `/users`;
		}
	});
}

function redirectToEditPage(chatId) {

	const deleteFileUrl = `/deleteChatMessage?chatId=${chatId}`;


	fetch(deleteFileUrl, {
		method: 'POST'
	}).then((response) => {

		if (response.ok) {
			window.location.href = `/users`;
		}
	});
}