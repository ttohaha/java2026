package blinov_first.exception;
/** Thrown when the connection pool encountered an error (pre-Spring version). */
public class PoolException extends Exception {
    public PoolException(String message)                  { super(message); }
    public PoolException(String message, Throwable cause) { super(message, cause); }
}
