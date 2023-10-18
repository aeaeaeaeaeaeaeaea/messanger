$(document).ready(function() {
    const modal = $("#myModal");
    const deleteButton = $("#deleteButton");
    const editButton = $("#editButton");
	var chatId;
	
    $(".chat-link").on('contextmenu', function(e) {
        e.preventDefault();
        var mess = document.getElementById('link');
		chatId = mess.getAttribute("data");
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
        if (!$(e.target).hasClass("chat-link")) {
            modal.hide();
        }
    });

    function deleteChat(chatId) {
        const deleteFileUrl = `/deleteChat?chatId=${chatId}`;
        fetch(deleteFileUrl, {
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
              /*  window.location.href = `/users`;*/
            }
        });
    }

    function redirectToEditPage(chatId) {
        const deleteFileUrl = `/deleteChatMessage?chatId=${chatId}`;
        fetch(deleteFileUrl, {
            method: 'POST'
        }).then((response) => {
            if (response.ok) {
               /* window.location.href = `/users`;*/
            }
        });
    }
});
