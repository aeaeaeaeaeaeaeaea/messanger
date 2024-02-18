function updateUserStatus() {
	// AJAX-запрос для обновления статуса пользователя
	var userId = $("#userInfoDiv1").attr("data-userid");


	$.get("/updateUserStatus/" + userId, function(data) {
		// Парсим данные из JSON
		var jsonData = JSON.parse(data);

		// Проверяем, является ли JSON пустым
		if ($.isEmptyObject(jsonData)) {
			// Если JSON пуст, выводим "last seen recently"
			$("#userInfoDiv #statusInfo").text("last seen recently");
		} else {
			// В противном случае, обновляем статус пользователя в вашем div
			$("#userInfoDiv #statusInfo").text(jsonData.connectionInfo);
		}
	});
}

// Вызываем функцию при загрузке страницы
$(document).ready(function() {
	updateUserStatus();

	
	setInterval(updateUserStatus, 3000);
});