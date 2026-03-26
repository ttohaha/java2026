document.addEventListener("DOMContentLoaded", function() {
    const form = document.querySelector("form");

    form.addEventListener("submit", function(event) {
        const login = document.querySelector('input[name="login"]').value;
        const pass = document.querySelector('input[name="password"]').value;

        if (login.trim() === "" || pass.trim() === "") {
            alert("Поля не должны быть пустыми!");
            event.preventDefault(); // Останавливает отправку формы
        }
    });

    console.log("Валидация JS готова");
});