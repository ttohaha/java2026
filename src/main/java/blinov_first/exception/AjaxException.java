package blinov_first.exception;
/** Thrown during AJAX request processing in the pre-Spring version. */
public class AjaxException extends Exception {
    public AjaxException(String message)                  { super(message); }
    public AjaxException(String message, Throwable cause) { super(message, cause); }
}
