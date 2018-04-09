package controllers;

import static java.util.concurrent.CompletableFuture.completedFuture;

import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.CaseDetailsDao;
import components.service.UserPermissionService;
import components.service.UserService;
import models.Application;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class AppGuardAction extends Action.Simple {

  private static final Logger LOGGER = LoggerFactory.getLogger(AppGuardAction.class);

  private static final String PATH_START = "/application/";
  private static final String PATTERN_START = "/application/$appId<[^/]+>/";

  private final ApplicationDao applicationDao;
  private final CaseDetailsDao caseDetailsDao;
  private final UserService userService;
  private final UserPermissionService userPermissionService;

  @Inject
  public AppGuardAction(ApplicationDao applicationDao,
                        CaseDetailsDao caseDetailsDao,
                        UserService userService,
                        UserPermissionService userPermissionService) {
    this.applicationDao = applicationDao;
    this.caseDetailsDao = caseDetailsDao;
    this.userService = userService;
    this.userPermissionService = userPermissionService;
  }

  @Override
  public CompletionStage<Result> call(Context ctx) {
    String path = ctx.request().path();
    String pattern = (String) ctx.args.get("ROUTE_PATTERN");
    if (StringUtils.startsWith(path, PATH_START) && StringUtils.startsWith(pattern, PATTERN_START)) {
      String appIdStart = StringUtils.removeStart(path, PATH_START);
      int index = appIdStart.indexOf("/");
      if (index != -1) {
        String appId = appIdStart.substring(0, index);
        Application application = applicationDao.getApplication(appId);
        if (application == null) {
          LOGGER.error("Unknown application id {}", appId);
          return error();
        } else {
          String currentUserId = userService.getCurrentUserId();
          boolean allowed = userPermissionService.canViewApplication(currentUserId, application);
          if (allowed) {
            boolean hasCase = caseDetailsDao.hasCase(appId);
            if (hasCase) {
              return delegate.call(ctx);
            } else {
              LOGGER.error("Application {} has no case", appId);
              return error();
            }
          } else {
            LOGGER.error("User {} has no access to application {}", currentUserId, appId);
            return error();
          }
        }
      } else {
        LOGGER.error("Path {} or pattern {} not valid ", path, pattern);
        return error();
      }
    } else {
      LOGGER.error("Path {} or pattern {} not valid ", path, pattern);
      return error();
    }
  }

  private CompletableFuture<Result> error() {
    return completedFuture(notFound("Unknown application."));
  }

}
