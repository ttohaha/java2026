package blinov_first.exception;
/** Thrown by command classes in the pre-Spring front-controller pattern. */
public class CommandException extends Exception {
    public CommandException(String message)                  { super(message); }
    public CommandException(String message, Throwable cause) { super(message, cause); }
}
