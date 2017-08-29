package controllers.action;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import models.RfiResponse;
import models.view.form.RfiResponseForm;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NoDuplicateRfiResponseCheck extends Action.Simple {

  private final FormFactory formFactory;
  private final RfiResponseDao rfiResponseDao;

  @Inject
  public NoDuplicateRfiResponseCheck(FormFactory formFactory, RfiResponseDao rfiResponseDao) {
    this.formFactory = formFactory;
    this.rfiResponseDao = rfiResponseDao;
  }

  @Override
  public CompletionStage<Result> call(Http.Context ctx) {
    Form<RfiResponseForm> rfiResponseForm = formFactory.form(RfiResponseForm.class).bindFromRequest();
    String rfiId = rfiResponseForm.data().get("rfiId");
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    if (rfiResponse != null) {
      return CompletableFuture.completedFuture(controllers.ApplicationDetailsController.badRequest());
    } else {
      return delegate.call(ctx);
    }
  }

}
