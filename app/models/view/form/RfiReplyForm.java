package models.view.form;

import play.data.validation.Constraints;

public class RfiReplyForm {

  public String delete;

  @Constraints.Required(message = "Please provide a message.")
  public String replyMessage;

  public String getDelete() {
    return delete;
  }

  public void setDelete(String delete) {
    this.delete = delete;
  }

  public String getReplyMessage() {
    return replyMessage;
  }

  public void setReplyMessage(String replyMessage) {
    this.replyMessage = replyMessage;
  }

}
