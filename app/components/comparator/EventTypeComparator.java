package components.comparator;

import java.util.Comparator;
import models.view.ApplicationItemView;
import models.view.NotificationView;

public class EventTypeComparator implements Comparator<ApplicationItemView> {

  @Override
  public int compare(ApplicationItemView o1, ApplicationItemView o2) {
    NotificationView left = o1.getForYourAttentionNotificationViews().get(0);
    NotificationView right = o2.getForYourAttentionNotificationViews().get(0);
    if (!left.getLinkText().equals(right.getLinkText())) {
      return left.getLinkText().compareToIgnoreCase(right.getLinkText());
    } else {
      return Long.compare(left.getCreatedTimestamp(), right.getCreatedTimestamp());
    }
  }

}
