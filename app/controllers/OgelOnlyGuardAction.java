package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.exceptions.UnknownParameterException;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;

public class OgelOnlyGuardAction extends Action.Simple {

  private final boolean ogelOnly;

  @Inject
  public OgelOnlyGuardAction(@Named("ogelOnly") boolean ogelOnly) {
    this.ogelOnly = ogelOnly;
  }

  @Override
  public CompletionStage<Result> call(Context ctx) {
    if (ogelOnly) {
      throw UnknownParameterException.unknownPath();
    } else {
      return delegate.call(ctx);
    }
  }

}
