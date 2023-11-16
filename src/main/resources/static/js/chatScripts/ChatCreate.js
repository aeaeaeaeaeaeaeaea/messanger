$(document).ready(function() {
    $(".chat-create").click(function(e) {
        e.preventDefault();

        var userId = $(this).data("user-id");
        var currentUser = $(this).data("current-user");

        const deleteFileUrl = `/chat?userId=${userId}&currentUser=${currentUser}`;

        fetch(deleteFileUrl, {
            method: 'POST'
        }).then(response => response.text())
        .then(chatId => {
            console.log("CHAT ID " + chatId);
            window.location.href = `/chat/${chatId}`;
        })
        
    });
});


