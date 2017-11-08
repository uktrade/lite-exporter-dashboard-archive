package components.exceptions;

public class UnexpectedStateException extends RuntimeException {

  public UnexpectedStateException(String message) {
    super(message);
  }

  public UnexpectedStateException(String message, Throwable cause) {
    super(message, cause);
  }

}
