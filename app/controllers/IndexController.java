package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import play.mvc.Result;

public class IndexController extends SamlController {

  private final boolean ogelOnly;

  @Inject
  public IndexController(@Named("ogelOnly") boolean ogelOnly) {
    this.ogelOnly = ogelOnly;
  }

  public Result index() {
    if (ogelOnly) {
      return redirect("/licences");
    } else {
      return redirect("/applications");
    }
  }

}
