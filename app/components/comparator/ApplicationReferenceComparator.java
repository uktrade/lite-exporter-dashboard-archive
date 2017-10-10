package components.comparator;

import java.util.Comparator;
import models.view.ApplicationItemView;

public class ApplicationReferenceComparator implements Comparator<ApplicationItemView> {

  @Override
  public int compare(ApplicationItemView o1, ApplicationItemView o2) {
    if (o1.getCaseReference() != null && o2.getCaseReference() != null) {
      return o1.getCaseReference().compareToIgnoreCase(o2.getCaseReference());
    } else if (o1.getCaseReference() == null && o2.getCaseReference() == null) {
      return o1.getApplicantReference().compareToIgnoreCase(o2.getApplicantReference());
    } else if (o1.getCaseReference() != null) {
      return 1;
    } else {
      return -1;
    }
  }

}
