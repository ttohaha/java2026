package blinov_first.controller;

/**
 * This class existed as a front-controller servlet in the pre-Spring version
 * of the application.  It has been superseded by Spring MVC controllers:
 *
 *   AuthController      — login, register, confirm, logout
 *   MainController      — main page, profile
 *   PhoneBookController — CRUD for phone entries
 *   MediaController     — file upload/download/delete
 *   LocaleController    — locale switching
 *
 * Retained as an empty placeholder so that any existing references
 * in IDE navigation or version control remain resolvable.
 *
 * @deprecated Replaced by Spring MVC @Controller classes.
 */
@Deprecated
public class Controller {
    // Intentionally empty — see JavaDoc above.
}
