package models.view;

public class ApplicationTabsView {

  private final boolean newRfi;
  private final boolean newMessage;
  private final boolean newDocument;

  public ApplicationTabsView(boolean newRfi, boolean newMessage, boolean newDocument) {
    this.newRfi = newRfi;
    this.newMessage = newMessage;
    this.newDocument = newDocument;
  }

  public boolean isNewRfi() {
    return newRfi;
  }

  public boolean isNewMessage() {
    return newMessage;
  }

  public boolean isNewDocument() {
    return newDocument;
  }

}
