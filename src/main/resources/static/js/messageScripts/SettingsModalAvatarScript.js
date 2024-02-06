// Функция для генерации рандомного цвета
function getRandomColor() {
	var letters = '0123456789ABCDEF';
	var color = '#';
	for (var i = 0; i < 6; i++) {
		color += letters[Math.floor(Math.random() * 16)];
	}
	return color;
}

// Функция для генерации круглой аватарки
function generateAvatar(username) {
	var canvas = document.createElement('canvas');
	var size = 100;
	canvas.width = size;
	canvas.height = size;
	var ctx = canvas.getContext('2d');

	// Рисуем круг с рандомным цветом
	ctx.beginPath();
	ctx.arc(size / 2, size / 2, size / 2, 0, 2 * Math.PI);
	ctx.fillStyle = getRandomColor();
	ctx.fill();

	// Добавляем первую букву никнейма
	ctx.fillStyle = 'white';
	ctx.font = '40px Arial';
	ctx.textAlign = 'center';
	ctx.textBaseline = 'middle';
	ctx.fillText(username.charAt(0), size / 2, size / 2);

	// Преобразуем канвас в изображение
	var avatarImage = new Image();
	avatarImage.src = canvas.toDataURL();

	return avatarImage;
}

// Получаем никнейм пользователя (замените на актуальное значение)
var username = 'john';

// Генерируем и отображаем аватарку
var avatar = generateAvatar(username);
document.getElementById('settings_modal_avatar').src = avatar.src;