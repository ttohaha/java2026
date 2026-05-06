package selenium;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BaseSeleniumTest {

    protected static final String BASE_URL      = "http://localhost:8080/blinov_first_war_exploded";
    protected static final String TEST_LOGIN    = "qwe";
    protected static final String TEST_PASSWORD = "qweqweqwe";

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(5);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    static void setupDriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        //!!!if you want to see the changes in the browser, comment out the line below.
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1280,800");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(3));
        wait = new WebDriverWait(driver, WAIT_TIMEOUT);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void navigateTo(String path) {
        driver.get(BASE_URL + path);
    }

    protected void login(String login, String password) {
        navigateTo("/index.jsp");
        driver.findElement(org.openqa.selenium.By.id("login")).sendKeys(login);
        driver.findElement(org.openqa.selenium.By.id("password")).sendKeys(password);

        org.openqa.selenium.JavascriptExecutor js = (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript("document.getElementById('loginForm').submit()");

        new org.openqa.selenium.support.ui.WebDriverWait(driver, java.time.Duration.ofSeconds(5))
                .until(org.openqa.selenium.support.ui.ExpectedConditions
                        .not(org.openqa.selenium.support.ui.ExpectedConditions.urlContains("index.jsp")));
    }
}
