package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.AppDataService;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationTabsViewService;
import components.service.MessageViewService;
import components.service.ReadDataService;
import components.service.UserService;
import java.util.List;
import models.AppData;
import models.ReadData;
import models.view.ApplicationSummaryView;
import models.view.ApplicationTabsView;
import models.view.MessageView;
import play.mvc.Result;
import views.html.messagesTab;

public class MessageTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final MessageViewService messageViewService;
  private final AppDataService appDataService;
  private final ApplicationTabsViewService applicationTabsViewService;
  private final UserService userService;
  private final ReadDataService readDataService;

  @Inject
  public MessageTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              MessageViewService messageViewService,
                              AppDataService appDataService,
                              ApplicationTabsViewService applicationTabsViewService,
                              UserService userService,
                              ReadDataService readDataService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.messageViewService = messageViewService;
    this.appDataService = appDataService;
    this.applicationTabsViewService = applicationTabsViewService;
    this.userService = userService;
    this.readDataService = readDataService;
  }

  public Result showMessages(String appId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    ReadData readData = readDataService.getReadData(userId, appData);
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appData);
    ApplicationTabsView applicationTabsView = applicationTabsViewService.getApplicationTabsView(appData, readData);
    List<MessageView> messageViews = messageViewService.getMessageViews(appData, readData);
    readDataService.updateMessageTabReadData(userId, readData);
    return ok(messagesTab.render(licenceApplicationAddress, applicationSummaryView, applicationTabsView, messageViews));
  }

}
