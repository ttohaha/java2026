package selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationPageTest extends BaseSeleniumTest {

    @Test
    void registrationPage_shouldBeAccessible_withoutLogin() {
        navigateTo("/pages/registration.jsp");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
            currentUrl.contains("registration"),
            "Registration page should be accessible without login"
        );
    }

    @Test
    void registrationPage_shouldDisplay_requiredFields() {
        navigateTo("/pages/registration.jsp");

        assertTrue(driver.findElement(By.id("login")).isDisplayed());
        assertTrue(driver.findElement(By.id("email")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());
    }

    @Test
    void registrationPage_shouldDisplay_passwordStrengthBars() {
        navigateTo("/pages/registration.jsp");

        assertTrue(
            driver.findElements(By.cssSelector(".strength-bar")).size() == 3,
            "Expected 3 password strength bars"
        );
    }

    @Test
    void registrationPage_shouldHaveLink_toLoginPage() {
        navigateTo("/pages/registration.jsp");

        assertTrue(
            driver.findElements(By.cssSelector("a[href*='index.jsp']")).size() > 0,
            "Expected link back to login page"
        );
    }

    @Test
    void loginPage_shouldHaveLink_toRegistrationPage() {
        navigateTo("/index.jsp");

        assertTrue(
            driver.findElements(By.cssSelector("a[href*='registration.jsp']")).size() > 0,
            "Expected link to registration page on login page"
        );
    }

    @Test
    void registrationPage_shouldDisplay_localeSwitcher() {
        navigateTo("/pages/registration.jsp");

        assertTrue(
            driver.findElements(By.cssSelector(".locale-btn")).size() >= 2,
            "Expected locale buttons on registration page"
        );
    }
}
