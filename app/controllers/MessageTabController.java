package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.ApplicationSummaryViewService;
import components.service.MessageViewService;
import components.service.RfiViewService;
import models.view.ApplicationSummaryView;
import models.view.MessageView;
import play.mvc.Result;
import views.html.messagesTab;

import java.util.List;

public class MessageTabController extends SamlController {

  private final String licenceApplicationAddress;
  private final ApplicationSummaryViewService applicationSummaryViewService;
  private final MessageViewService messageViewService;
  private final RfiViewService rfiViewService;

  @Inject
  public MessageTabController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                              ApplicationSummaryViewService applicationSummaryViewService,
                              MessageViewService messageViewService,
                              RfiViewService rfiViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationSummaryViewService = applicationSummaryViewService;
    this.messageViewService = messageViewService;
    this.rfiViewService = rfiViewService;
  }

  public Result showMessages(String appId) {
    ApplicationSummaryView applicationSummaryView = applicationSummaryViewService.getApplicationSummaryView(appId);
    List<MessageView> messageViews = messageViewService.getMessageViews(appId);
    int rfiViewCount = rfiViewService.getRfiViewCount(appId);
    return ok(messagesTab.render(licenceApplicationAddress, applicationSummaryView, rfiViewCount, messageViews));
  }

}
