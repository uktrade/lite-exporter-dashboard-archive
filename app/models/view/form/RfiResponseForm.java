package models.view.form;

import play.data.validation.Constraints;

public class RfiResponseForm {

  public String rfiId;

  @Constraints.Required
  public String responseMessage;

}
