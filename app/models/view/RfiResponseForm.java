package models.view;

import play.data.validation.Constraints;

public class RfiResponseForm {

  @Constraints.Required
  public String responseMessage;

}
