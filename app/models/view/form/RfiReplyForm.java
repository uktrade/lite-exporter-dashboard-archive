package models.view.form;

import play.data.validation.Constraints;

public class RfiReplyForm {

  public String delete;

  @Constraints.Required(message = "Please provide a message.")
  public String replyMessage;

}
