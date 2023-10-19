$(document).ready(function() {
    const modal = $("#myModal");
    const deleteButton = $("#deleteButton");
    const editButton = $("#editButton");
	
    $(".chat-link").on('contextmenu', function(e) {
        e.preventDefault();
        var mess = $(this);
        var chatId = mess.attr("data");
        deleteButton.data("chatId", chatId); // Сохраняем chatId в data атрибуте deleteButton
        editButton.data("chatId", chatId); // Сохраняем chatId в data атрибуте editButton
        modal.css({ top: e.pageY + "px", left: e.pageX + "px" });
        modal.show();
    });

    $(".close").on("click", function() {
        modal.hide();
    });

    deleteButton.on("click", function() {
        var chatId = deleteButton.data("chatId"); // Получаем chatId из data атрибута
        deleteChat(chatId);
        modal.hide();
    });

    editButton.on("click", function() {
        var chatId = editButton.data("chatId"); // Получаем chatId из data атрибута
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
});

