package components.comparator;

import models.view.ApplicationItemView;

import java.util.Comparator;

public class ApplicationDateComparator implements Comparator<ApplicationItemView> {

  @Override
  public int compare(ApplicationItemView o1, ApplicationItemView o2) {
    if (o1.getSubmittedTimestamp() != null && o2.getSubmittedTimestamp() != null) {
      return o1.getSubmittedTimestamp().compareTo(o2.getSubmittedTimestamp());
    } else if (o1.getSubmittedTimestamp() == null && o2.getSubmittedTimestamp() == null) {
      return o1.getCreatedTimestamp().compareTo(o2.getCreatedTimestamp());
    } else {
      return o1.getSubmittedTimestamp() == null ? 1 : -1;
    }
  }

}
