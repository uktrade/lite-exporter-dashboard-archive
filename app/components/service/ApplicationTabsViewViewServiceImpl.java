package components.service;

import components.util.ApplicationUtil;
import models.AppData;
import models.ReadData;
import models.view.ApplicationTabsView;

public class ApplicationTabsViewViewServiceImpl implements ApplicationTabsViewService {

  @Override
  public ApplicationTabsView getApplicationTabsView(AppData appData, ReadData readData) {
    boolean newRfi = !ApplicationUtil.getOpenRfiList(appData).isEmpty();
    boolean newDocument = isNewDocument(readData);
    boolean isNewMessage = isNewMessage(readData);
    return new ApplicationTabsView(newRfi, isNewMessage, newDocument);
  }

  private boolean isNewDocument(ReadData readData) {
    boolean isNewOutcomeDocuments = !readData.getUnreadOutcomeIds().isEmpty();
    boolean isNewInformNotifications = !readData.getUnreadInformNotificationIds().isEmpty();
    return isNewOutcomeDocuments || isNewInformNotifications;
  }

  private boolean isNewMessage(ReadData readData) {
    boolean isNewStopMessage = readData.getUnreadStopNotificationId() != null;
    boolean isNewDelayMessage = readData.getUnreadDelayNotificationId() != null;
    boolean isNewWithdrawalRejectionMessage = !readData.getUnreadWithdrawalRejectionIds().isEmpty();
    return isNewStopMessage || isNewDelayMessage || isNewWithdrawalRejectionMessage;
  }

}
