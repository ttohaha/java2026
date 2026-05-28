package blinov_first.exception;

/**
 * Thrown when the DAO layer encounters a data-access error.
 */
public class DaoException extends Exception {

    public DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }
}
