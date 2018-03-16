package message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import components.dao.NotificationDao;
import components.dao.RfiDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.message.ConsumerRoutingKey;
import components.message.MessageHandler;
import components.message.MessageHandlerImpl;
import components.service.EscapeHtmlServiceImpl;
import models.Notification;
import models.Rfi;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationDelay;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationStop;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalAccept;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalReject;

import java.util.Collections;
import java.util.List;

public class EscapeMessageTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final List<String> RECIPIENTS = Collections.singletonList("recipient");
  private static final String UNESCAPED = "<title>Title</title><i>Italic</i>";
  private static final String ESCAPED = "Title<em>Italic</em>";

  private final RfiDao rfiDao = mock(RfiDao.class);
  private final NotificationDao notificationDao = mock(NotificationDao.class);
  private final WithdrawalRejectionDao withdrawalRejectionDao = mock(WithdrawalRejectionDao.class);
  private final WithdrawalApprovalDao withdrawalApprovalDao = mock(WithdrawalApprovalDao.class);
  private final MessageHandler messageHandler = new MessageHandlerImpl(rfiDao,
      null,
      notificationDao,
      null,
      null,
      withdrawalRejectionDao,
      withdrawalApprovalDao,
      null,
      null,
      new EscapeHtmlServiceImpl());

  @Test
  public void shouldEscapeRfi() {
    DashboardRfiCreate dashboardRfiCreate = new DashboardRfiCreate();
    dashboardRfiCreate.setAppId("appId");
    dashboardRfiCreate.setCaseRef("caseRef");
    dashboardRfiCreate.setId("id");
    dashboardRfiCreate.setMessage(UNESCAPED);
    dashboardRfiCreate.setCreatedByUserId("createdByUserId");
    dashboardRfiCreate.setCreatedTimestamp(0L);
    dashboardRfiCreate.setDeadlineTimestamp(0L);
    dashboardRfiCreate.setRecipientUserIds(RECIPIENTS);
    messageHandler.handleMessage(ConsumerRoutingKey.RFI.toString(), toJson(dashboardRfiCreate));

    ArgumentCaptor<Rfi> captor = ArgumentCaptor.forClass(Rfi.class);
    verify(rfiDao).insertRfi(captor.capture());

    Rfi rfi = captor.getValue();
    assertThat(rfi.getMessage()).isEqualTo(ESCAPED);
  }

  @Test
  public void shouldEscapeDelayNotification() {
    DashboardNotificationDelay dashboardNotificationDelay = new DashboardNotificationDelay();
    dashboardNotificationDelay.setId("id");
    dashboardNotificationDelay.setCaseRef("caseRef");
    dashboardNotificationDelay.setRecipientUserIds(RECIPIENTS);
    dashboardNotificationDelay.setCreatedTimestamp(0L);
    dashboardNotificationDelay.setMessage(UNESCAPED);
    messageHandler.handleMessage(ConsumerRoutingKey.DELAY_NOTIFICATION.toString(), toJson(dashboardNotificationDelay));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationDao).insertNotification(captor.capture());

    Notification notification = captor.getValue();
    assertThat(notification.getMessage()).isEqualTo(ESCAPED);
  }

  @Test
  public void shouldEscapeStopNotification() {
    DashboardNotificationStop dashboardNotificationStop = new DashboardNotificationStop();
    dashboardNotificationStop.setId("id");
    dashboardNotificationStop.setCaseRef("caseRef");
    dashboardNotificationStop.setCreatedByUserId("createdByUserId");
    dashboardNotificationStop.setRecipientUserIds(RECIPIENTS);
    dashboardNotificationStop.setCreatedTimestamp(0L);
    dashboardNotificationStop.setMessage(UNESCAPED);
    messageHandler.handleMessage(ConsumerRoutingKey.STOP_NOTIFICATION.toString(), toJson(dashboardNotificationStop));

    ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationDao).insertNotification(captor.capture());

    Notification notification = captor.getValue();
    assertThat(notification.getMessage()).isEqualTo(ESCAPED);
  }

  @Test
  public void shouldEscapeWithdrawalAccept() {
    DashboardWithdrawalAccept dashboardWithdrawalAccept = new DashboardWithdrawalAccept();
    dashboardWithdrawalAccept.setId("id");
    dashboardWithdrawalAccept.setAppId("appId");
    dashboardWithdrawalAccept.setRecipientUserIds(RECIPIENTS);
    dashboardWithdrawalAccept.setCreatedTimestamp(0L);
    dashboardWithdrawalAccept.setMessage(UNESCAPED);
    dashboardWithdrawalAccept.setCreatedByUserId("createdByUserId");
    messageHandler.handleMessage(ConsumerRoutingKey.WITHDRAWAL_ACCEPT.toString(), toJson(dashboardWithdrawalAccept));

    ArgumentCaptor<WithdrawalApproval> captor = ArgumentCaptor.forClass(WithdrawalApproval.class);
    verify(withdrawalApprovalDao).insertWithdrawalApproval(captor.capture());

    WithdrawalApproval withdrawalApproval = captor.getValue();
    assertThat(withdrawalApproval.getMessage()).isEqualTo(ESCAPED);
  }

  @Test
  public void shouldEscapeWithdrawalRejection() {
    DashboardWithdrawalReject dashboardWithdrawalReject = new DashboardWithdrawalReject();
    dashboardWithdrawalReject.setAppId("appId");
    dashboardWithdrawalReject.setCreatedByUserId("createdByUserId");
    dashboardWithdrawalReject.setMessage(UNESCAPED);
    dashboardWithdrawalReject.setRecipientUserIds(Lists.newArrayList());

    messageHandler.handleMessage(ConsumerRoutingKey.WITHDRAWAL_REJECTION.toString(), toJson(dashboardWithdrawalReject));

    ArgumentCaptor<WithdrawalRejection> captor = ArgumentCaptor.forClass(WithdrawalRejection.class);
    verify(withdrawalRejectionDao).insertWithdrawalRejection(captor.capture());

    WithdrawalRejection withdrawalRejection = captor.getValue();
    assertThat(withdrawalRejection.getMessage()).isEqualTo(ESCAPED);
  }

  private String toJson(Object object) {
    try {
      return MAPPER.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
