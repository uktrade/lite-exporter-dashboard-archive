package models.view.form;

import play.data.validation.Constraints;

public class AmendApplicationForm {

  @Constraints.Required(message = "Sort of change is required.")
  public String action;

  @Constraints.Required(message = "Message is required.")
  public String message;

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

}
