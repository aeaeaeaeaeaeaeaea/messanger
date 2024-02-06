// Функция для генерации рандомного цвета
function getRandomColor() {
	var letters = '0123456789ABCDEF';
	var color = '#';
	for (var i = 0; i < 6; i++) {
		color += letters[Math.floor(Math.random() * 16)];
	}
	return color;
}

// Функция для генерации аватарки
function generateAvatar(username) {
	var canvas = document.createElement('canvas');
	canvas.width = 100;
	canvas.height = 100;
	var ctx = canvas.getContext('2d');

	// Заполняем канвас рандомным цветом
	ctx.fillStyle = getRandomColor();
	ctx.fillRect(0, 0, canvas.width, canvas.height);

	// Добавляем первую букву никнейма
	ctx.fillStyle = 'white';
	ctx.font = '40px Arial';
	ctx.fillText(username.charAt(0), 30, 60);

	// Преобразуем канвас в изображение
	var avatarImage = new Image();
	avatarImage.src = canvas.toDataURL();

	return avatarImage;
}

// Получаем никнейм пользователя (замените на актуальное значение)
var username = 'john';

// Генерируем и отображаем аватарку
var avatar = generateAvatar(username);
document.getElementById('profile_modal_avatar').src = avatar.src;