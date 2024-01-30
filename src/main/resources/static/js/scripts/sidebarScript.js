/**
 * 
 */
// Функция для отображения модального окна
function showModal() {
	var modal = document.getElementById("side_modal");
	modal.style.display = "block";
	// Добавляем обработчик событий для клика по документу
	document.addEventListener('click', closeIfOutsideModal);
}

// Функция для скрытия модального окна
function hideModal() {
	var modal = document.getElementById("side_modal");
	modal.style.display = "none";
	// Удаляем обработчик событий после закрытия модального окна
	document.removeEventListener('click', closeIfOutsideModal);
}

// Функция для закрытия модального окна при клике вне его
function closeIfOutsideModal(event) {
	var modal = document.getElementById("side_modal");
	if (!modal.contains(event.target)) {
		hideModal();
	}
}