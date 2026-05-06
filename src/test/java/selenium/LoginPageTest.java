package selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginPageTest extends BaseSeleniumTest {

    private static final String CMD_LOGOUT     = "/controller?command=logout";
    private static final String URL_PART_INDEX = "index.jsp";
    private static final String URL_PART_MAIN  = "main";

    @Test
    void loginPage_shouldBeAccessible_withoutAuthentication() {
        navigateTo("/index.jsp");

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.contains(URL_PART_INDEX),
                "Login page should be accessible without authentication"
        );
    }

    @Test
    void loginPage_shouldDisplay_loginAndPasswordFields() {
        navigateTo("/index.jsp");

        assertTrue(driver.findElement(By.id("login")).isDisplayed());
        assertTrue(driver.findElement(By.id("password")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());
    }

    @Test
    void login_shouldRedirect_toMainPage_onValidCredentials() {
        login(TEST_LOGIN, TEST_PASSWORD);

        String currentUrl = driver.getCurrentUrl();
        boolean onMainPage = currentUrl.contains(URL_PART_MAIN)
                || currentUrl.contains("list_entries")
                || currentUrl.contains("controller");

        assertTrue(onMainPage, "Successful login should redirect away from the index page");
    }

    @Test
    void login_shouldShowError_onInvalidCredentials() {
        navigateTo("/index.jsp");

        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "document.getElementById('login').value    = arguments[0];" +
                        "document.getElementById('password').value = arguments[1];",
                "invalid_user_xyz_99", "wrong_password_xyz"
        );
        js.executeScript("document.getElementById('loginForm').requestSubmit()");

        // Invalid login causes a server-side forward back to index.jsp content.
        // The URL stays at /controller (forward, not redirect), so we wait
        // for the error alert to appear rather than for a URL change.
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".alert-danger")));

        assertTrue(
                driver.findElement(By.cssSelector(".alert-danger")).isDisplayed(),
                "Invalid credentials should display an error alert on the login page"
        );
    }

    @Test
    void loginPage_shouldDisplay_localeSwitcher() {
        navigateTo("/index.jsp");

        assertTrue(
                driver.findElements(By.cssSelector(".locale-btn")).size() >= 2,
                "Login page should have locale buttons"
        );
    }

    @Test
    void loginPage_shouldHaveLink_toRegistrationPage() {
        navigateTo("/index.jsp");

        assertTrue(
                !driver.findElements(By.cssSelector("a[href*='registration.jsp']")).isEmpty(),
                "Login page should have a link to the registration page"
        );
    }

    @Test
    void logout_shouldRedirect_toLoginPage() {
        login(TEST_LOGIN, TEST_PASSWORD);

        navigateTo(CMD_LOGOUT);

        wait.until(ExpectedConditions.urlContains(URL_PART_INDEX));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.contains(URL_PART_INDEX),
                "Expected redirect to login page after logout"
        );
    }
}