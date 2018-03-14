package controllers;

import com.google.inject.Inject;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.MessageViewService;
import components.service.ReadDataService;
import components.service.UserService;
import models.AppData;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.MessageView;
import play.mvc.Result;
import play.mvc.With;
import views.html.messagesTab;

import java.util.List;

@With(AppGuardAction.class)
public class MessageTabController extends SamlController {

  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final MessageViewService messageViewService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;
  private final messagesTab messagesTab;

  @Inject
  public MessageTabController(ApplicationSummaryViewService applicationSummaryViewService,
                              MessageViewService messageViewService,
                              AppDataService appDataService,
                              ApplicationTabsViewService applicationTabsViewService,
                              UserService userService,
                              ReadDataService readDataService,
                              messagesTab messagesTab) {
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.messageViewService = messageViewService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
    this.messagesTab = messagesTab;
  }

  public Result showMessages(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<MessageView> messageViews = messageViewService.getMessageViews(appData, readData);
    readDataService.updateMessageTabReadData(userId, appData, readData);
    return ok(messagesTab.render(applicationSummaryView, applicationTabsView, messageViews));
  }

}
