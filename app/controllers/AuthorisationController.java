package controllers;

import static play.mvc.Results.forbidden;
import static play.mvc.Results.ok;

import components.common.CommonContextAction;
import play.mvc.Result;
import play.mvc.With;
import views.html.loggedOut;
import views.html.unauthorised;

@With(CommonContextAction.class)
public class AuthorisationController {

  public Result unauthorised() {
    return forbidden(unauthorised.render());
  }

  public Result loggedOut() {
    return ok(loggedOut.render());
  }

}
