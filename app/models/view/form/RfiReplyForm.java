package models.view.form;

import play.data.validation.Constraints;

public class RfiReplyForm {

  public String rfiId;

  @Constraints.Required(message = "Please provide a message.")
  public String replyMessage;

  public String getRfiId() {
    return rfiId;
  }

  public void setRfiId(String rfiId) {
    this.rfiId = rfiId;
  }

  public String getReplyMessage() {
    return replyMessage;
  }

  public void setReplyMessage(String replyMessage) {
    this.replyMessage = replyMessage;
  }

}
