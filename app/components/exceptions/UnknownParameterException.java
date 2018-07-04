package components.exceptions;

public class UnknownParameterException extends RuntimeException {

  private UnknownParameterException(String message) {
    super(message);
  }

  public static UnknownParameterException unknownOgelId(String ogelId) {
    return new UnknownParameterException("Unknown ogelId " + ogelId);
  }

  public static UnknownParameterException unknownSielId(String sielId) {
    return new UnknownParameterException("Unknown sielId " + sielId);
  }

  public static UnknownParameterException unknownPath() {
    return new UnknownParameterException("Unknown path");
  }

}
