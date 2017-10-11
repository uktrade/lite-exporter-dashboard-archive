package components.comparator;

import java.util.Comparator;
import models.view.ApplicationItemView;

public class DestinationComparator implements Comparator<ApplicationItemView> {

  private static final ApplicationReferenceComparator APPLICATION_REFERENCE_COMPARATOR = new ApplicationReferenceComparator();

  @Override
  public int compare(ApplicationItemView o1, ApplicationItemView o2) {
    if (o1.getDestination().equals(o2.getDestination())) {
      return APPLICATION_REFERENCE_COMPARATOR.compare(o1, o2);
    } else {
      return o1.getDestination().compareToIgnoreCase(o2.getDestination());
    }
  }

}
