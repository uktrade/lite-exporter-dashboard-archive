package models.view.form;

import play.data.validation.Constraints;

public class AmendApplicationForm {

  public String action;

  @Constraints.Required
  public String message;

}
