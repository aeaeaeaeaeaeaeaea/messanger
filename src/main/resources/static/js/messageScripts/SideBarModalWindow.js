function showModal1() {
	var modal = document.getElementById("main-modal1");
	modal.style.display = "block";
	// Добавляем обработчик событий для клика по документу
	document.addEventListener('click', closeIfOutsideModal);
}

// Функция для скрытия модального окна
function hideModal1() {
	var modal = document.getElementById("main-modal1");
	modal.style.display = "none";
	// Удаляем обработчик событий после закрытия модального окна
	document.removeEventListener('click', closeIfOutsideModal);
}

// Функция для закрытия модального окна при клике вне его
function closeIfOutsideModal1(event) {
	var modal = document.getElementById("main-modal1");
	if (!modal.contains(event.target)) {
		hideModal1();
	}
}