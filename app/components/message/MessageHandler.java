package components.message;

public interface MessageHandler {

  boolean handleMessage(String routingKey, String message);

}
