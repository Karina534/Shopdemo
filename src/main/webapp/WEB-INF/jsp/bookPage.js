let currentIndex = 0;

function updateSlide() {
    const images = document.querySelectorAll(".carousel-images img");

    // Сбрасываем класс active у всех изображений
    images.forEach((img) => img.classList.remove("active"));

    // Добавляем класс active текущему изображению
    images[currentIndex].classList.add("active");
}

function nextSlide() {
    const images = document.querySelectorAll(".carousel-images img");
    const totalImages = images.length;

    // Переход к следующему изображению
    currentIndex = (currentIndex + 1) % totalImages;
    updateSlide();
}

function prevSlide() {
    const images = document.querySelectorAll(".carousel-images img");
    const totalImages = images.length;

    // Переход к предыдущему изображению
    currentIndex = (currentIndex - 1 + totalImages) % totalImages;
    updateSlide();
}

// Инициализация: отображение первого изображения
document.addEventListener("DOMContentLoaded", () => {
    const images = document.querySelectorAll(".carousel-images img");
    if (images.length > 0) {
        images[0].classList.add("active");
    }
});
