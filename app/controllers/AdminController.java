package controllers;

import static play.mvc.Results.ok;

import actions.BasicAuthAction;
import play.mvc.Result;
import play.mvc.With;

@With(BasicAuthAction.class)
public class AdminController {

  public Result buildInfo() {
    return ok(buildinfo.BuildInfo$.MODULE$.toJson()).as("application/json");
  }

  public Result ping() {
   return ok("pong");
  }
}
