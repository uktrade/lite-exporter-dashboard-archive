package controllers;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import components.dao.ApplicationDao;
import components.service.UserPrivilegeService;
import components.service.UserService;
import java.util.concurrent.CompletionStage;
import models.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

@Singleton
public class AppGuardAction extends Action.Simple {

  private static final String PATH_START = "/application/";
  private static final String PATTERN_START = "/application/$appId<.+>/";

  private static final Logger LOGGER = LoggerFactory.getLogger(AppGuardAction.class);

  private final ApplicationDao applicationDao;
  private final UserService userService;
  private final UserPrivilegeService userPrivilegeService;

  @Inject
  public AppGuardAction(ApplicationDao applicationDao,
                        UserService userService,
                        UserPrivilegeService userPrivilegeService) {
    this.applicationDao = applicationDao;
    this.userService = userService;
    this.userPrivilegeService = userPrivilegeService;
  }

  @Override
  public CompletionStage<Result> call(Context ctx) {
    LOGGER.error("code: " + hashCode());
    String path = ctx.request().path();
    String pattern = (String) ctx.args.get("ROUTE_PATTERN");
    if (StringUtils.startsWith(path, PATH_START) && StringUtils.startsWith(pattern, PATTERN_START)) {
      String appIdStart = StringUtils.removeStart(path, PATH_START);
      int index = appIdStart.indexOf("/");
      if (index > 0) {
        String appId = appIdStart.substring(0, index);
        Application application = applicationDao.getApplication(appId);
        boolean allowed = userPrivilegeService.isAccessAllowed(userService.getCurrentUserId(), application.getCustomerId());
        LOGGER.error("allowed: " + allowed);
      }
    }
    return delegate.call(ctx);
  }
}
