package models.view;

import java.util.List;

public class StatusTrackerView {

  private final List<StatusItemView> originalStatusItemViews;
  private final List<List<StatusItemView>> caseStatusItemViews;

  public StatusTrackerView(List<StatusItemView> originalStatusItemViews, List<List<StatusItemView>> caseStatusItemViews) {
    this.originalStatusItemViews = originalStatusItemViews;
    this.caseStatusItemViews = caseStatusItemViews;
  }

  public List<StatusItemView> getOriginalStatusItemViews() {
    return originalStatusItemViews;
  }

  public List<List<StatusItemView>> getCaseStatusItemViews() {
    return caseStatusItemViews;
  }

}
