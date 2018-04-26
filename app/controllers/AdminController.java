package controllers;

import actions.BasicAuthAction;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.With;

@With(BasicAuthAction.class)
public class AdminController {

  public Result buildInfo() {
    return Results.ok(buildinfo.BuildInfo$.MODULE$.toJson()).as("application/json");
  }

}
