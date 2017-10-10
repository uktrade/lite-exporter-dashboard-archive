package components.util;

import java.util.Comparator;
import models.Notification;
import models.Outcome;
import models.Rfi;
import models.WithdrawalRejection;
import models.view.CompanySelectItemView;
import models.view.MessageView;
import models.view.NotificationView;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

public class Comparators {

  public static final Comparator<Rfi> RFI_RECEIVED = Comparator.comparing(Rfi::getReceivedTimestamp);
  public static final Comparator<Rfi> RFI_RECEIVED_REVERSED = Comparator.comparing(Rfi::getReceivedTimestamp).reversed();

  public static final Comparator<Outcome> OUTCOME_CREATED_REVERSED = Comparator.comparing(Outcome::getCreatedTimestamp).reversed();
  public static final Comparator<MessageView> MESSAGE_VIEW_CREATED_REVERSED = Comparator.comparing(MessageView::getCreatedTimestamp).reversed();
  public static final Comparator<WithdrawalRequest> WITHDRAWAL_REQUEST_CREATED = Comparator.comparing(WithdrawalRequest::getCreatedTimestamp);
  public static final Comparator<WithdrawalRequest> WITHDRAWAL_REQUEST_CREATED_REVERSED = Comparator.comparing(WithdrawalRequest::getCreatedTimestamp);
  public static final Comparator<WithdrawalRejection> WITHDRAWAL_REJECTION_CREATED = Comparator.comparing(WithdrawalRejection::getCreatedTimestamp);
  public static final Comparator<WithdrawalRejection> WITHDRAWAL_REJECTION_CREATED_REVERSED = Comparator.comparing(WithdrawalRejection::getCreatedTimestamp).reversed();
  public static final Comparator<Notification> NOTIFICATION_CREATED = Comparator.comparing(Notification::getCreatedTimestamp);
  public static final Comparator<Notification> NOTIFICATION_CREATED_REVERSED = Comparator.comparing(Notification::getCreatedTimestamp).reversed();
  public static final Comparator<NotificationView> NOTIFICATION_VIEW_CREATED = Comparator.comparing(NotificationView::getCreatedTimestamp);
  public static final Comparator<NotificationView> NOTIFICATION_VIEW_CREATED_REVERSED = Comparator.comparing(NotificationView::getCreatedTimestamp).reversed();

  public static final Comparator<NotificationView> LINK_TEXT = Comparator.comparing(NotificationView::getLinkText);
  public static final Comparator<CompanySelectItemView> COMPANY_NAME = Comparator.comparing(CompanySelectItemView::getCompanyName);

}