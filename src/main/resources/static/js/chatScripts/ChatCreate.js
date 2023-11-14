$(document).ready(function() {
	$(".chat-create").click(function(e) {
		e.preventDefault();

		var mess = document.getElementById('chat');
		var userId = mess.getAttribute("userId");
		var currentUser = mess.getAttribute("currentUser");

		const deleteFileUrl = `/chat?userId=${userId}&currentUser=${currentUser}`;
		
		console.log("USER ID " + userId)

		fetch(deleteFileUrl, {
			method: 'POST'
		}).then((chatId) => {
			/*window.location.href = `/chat/${chatId}`;*/
		});

	});
});