package models.view.form;

import play.data.validation.Constraints;

public class RfiResponseForm {

  public String rfiId;

  @Constraints.Required(message = "Please provide a message.")
  public String responseMessage;

}
