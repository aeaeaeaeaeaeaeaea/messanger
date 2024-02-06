// Обработка события изменения значения input
$('#fileInput').change(function() {
	// Проверка, что файл выбран
	if (this.files.length > 0) {
		// Создание объекта FormData для отправки файла
		var formData = new FormData();
		formData.append('file', this.files[0]);

		// Отправка файла по указанному адресу (в данном случае, '/avatar')
		$.ajax({
			url: '/avatar',
			type: 'POST',
			data: formData,
			processData: false,  // Не обрабатывать данные
			contentType: false,  // Не устанавливать тип контента
			success: function(response) {
				window.location.href = `/users`;
			}
		});
	}
});