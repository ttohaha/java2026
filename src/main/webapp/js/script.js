document.addEventListener("DOMContentLoaded", function () {
    const mainForm = document.querySelector("form");

    if (mainForm) {
        mainForm.addEventListener("submit", function (event) {
            const loginInput = document.querySelector("input[name='login']");
            const passwordInput = document.querySelector("input[name='password']");
            const emailInput = document.querySelector("input[name='email']");

            let isValid = true;
            let errorMessage = "";

            if (loginInput && loginInput.value.trim().length < 3) {
                errorMessage += "Login must be at least 3 characters long.\n";
                isValid = false;
            }

            if (passwordInput && passwordInput.value.length < 6) {
                errorMessage += "Password must be at least 6 characters long.\n";
                isValid = false;
            }

            if (emailInput) {
                const emailValue = emailInput.value.trim();
                if (!emailValue.includes("@") || emailValue.length < 5) {
                    errorMessage += "Please enter a valid email address.\n";
                    isValid = false;
                }
            }

            if (!isValid) {
                alert(errorMessage);
                event.preventDefault();
            }
        });
    }
});