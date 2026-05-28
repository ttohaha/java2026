package blinov_first.exception;

/**
 * Thrown when the service layer encounters a business-logic or infrastructure error.
 */
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
