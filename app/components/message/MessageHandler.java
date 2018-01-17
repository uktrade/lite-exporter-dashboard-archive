package components.message;

public interface MessageHandler {

  boolean handleMessage(String type, String message);

}
