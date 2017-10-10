package components.comparator;

import java.util.Comparator;
import models.view.ApplicationItemView;

public class ApplicationStatusComparator implements Comparator<ApplicationItemView> {

  @Override
  public int compare(ApplicationItemView o1, ApplicationItemView o2) {
    if (!o1.getApplicationStatus().equals(o2.getApplicationStatus())) {
      return o1.getApplicationStatus().compareToIgnoreCase(o2.getApplicationStatus());
    } else {
      return Long.compare(o1.getApplicationStatusTimestamp(), o2.getApplicationStatusTimestamp());
    }
  }

}
