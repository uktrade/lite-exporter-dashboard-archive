package controllers;

import static play.mvc.Results.ok;

import actions.BasicAuthAction;
import com.google.inject.Inject;
import components.service.PingService;
import models.admin.PingResult;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import play.mvc.With;


public class AdminController {

  private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(AdminController.class);
  private final PingService pingService;

  private final String PING_XML_TEMPLATE = "<pingdom_http_custom_check><status>%s</status><detail>%s</detail></pingdom_http_custom_check>";

  @Inject
  public AdminController(PingService pingService) {
    this.pingService = pingService;
  }

  @With(BasicAuthAction.class)
  public Result buildInfo() {
    return ok(buildinfo.BuildInfo$.MODULE$.toJson()).as("application/json");
  }

  @With(BasicAuthAction.class)
  public Result cascadePing() {
    LOGGER.info("Admin check request received - getting results from dependent service...");
    PingResult result = pingService.pingServices();
    return ok(String.format(PING_XML_TEMPLATE, result.getStatus(), result.getDetail())).as("application/xml");
  }
}
