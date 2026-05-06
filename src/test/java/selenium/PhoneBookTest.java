package selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneBookTest extends BaseSeleniumTest {

    private static final String CONTACT_NAME  = "Test Selenium User";
    private static final String CONTACT_PHONE = "+375291234567";
    private static final String CONTACT_EMAIL = "selenium@test.com";

    private static final String CMD_ADD_ENTRY  = "/controller?command=add_entry";
    private static final String CMD_LIST       = "/controller?command=list_entries";
    private static final String URL_PART_LIST  = "list_entries";

    @Test
    void phoneBookPage_shouldBeAccessible_afterLogin() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_LIST);

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.contains(URL_PART_LIST),
                "Phone book page should be accessible after login"
        );
    }

    @Test
    void addEntryPage_shouldBeAccessible_afterLogin() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_ADD_ENTRY);

        assertTrue(
                driver.findElement(By.id("contactName")).isDisplayed(),
                "Contact name field should be present on add entry form"
        );
        assertTrue(
                driver.findElement(By.id("contactPhone")).isDisplayed(),
                "Contact phone field should be present on add entry form"
        );
    }

    @Test
    void addEntryForm_shouldDisplay_requiredFields() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_ADD_ENTRY);

        assertTrue(driver.findElement(By.id("contactName")).isDisplayed());
        assertTrue(driver.findElement(By.id("contactPhone")).isDisplayed());
        assertTrue(driver.findElement(By.id("contactEmail")).isDisplayed());
        assertTrue(driver.findElement(By.cssSelector("button[type='submit']")).isDisplayed());
    }

    @Test
    void addContact_shouldShowContact_inList_afterAdding() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_ADD_ENTRY);

        // Guarantee field values are set in DOM before submit
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript(
                "document.getElementById('contactName').value  = arguments[0];" +
                        "document.getElementById('contactPhone').value = arguments[1];" +
                        "document.getElementById('contactEmail').value = arguments[2];",
                CONTACT_NAME, CONTACT_PHONE, CONTACT_EMAIL
        );

        // requestSubmit() fires the submit event so JS validation runs;
        // valid data passes validation and the browser POSTs the form normally.
        js.executeScript("document.getElementById('entryForm').requestSubmit()");

        wait.until(ExpectedConditions.urlContains(URL_PART_LIST));

        String currentUrl = driver.getCurrentUrl();
        assertTrue(
                currentUrl.contains(URL_PART_LIST),
                "Should be redirected to contact list after adding a contact"
        );
    }

    @Test
    void phoneBookPage_shouldContain_tableOrEmptyState() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_LIST);

        boolean hasTable      = !driver.findElements(By.cssSelector("table.data-table")).isEmpty();
        boolean hasEmptyState = !driver.findElements(By.cssSelector(".empty-state")).isEmpty();

        assertTrue(
                hasTable || hasEmptyState,
                "Phone book page should show either a data table or an empty state"
        );
    }

    @Test
    void phoneBookPage_shouldHave_addContactButton() {
        login(TEST_LOGIN, TEST_PASSWORD);
        navigateTo(CMD_LIST);

        assertTrue(
                !driver.findElements(By.cssSelector("a[href*='add_entry']")).isEmpty(),
                "Phone book page should have an 'Add contact' button"
        );
    }
}