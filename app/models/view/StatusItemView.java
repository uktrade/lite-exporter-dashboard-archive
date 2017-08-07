package models.view;

import java.util.List;

public class StatusItemView {

  private final String status;
  private final String statusExplanation;
  private final String processLabel;
  private final String processDescription;
  private final List<StatusItemRfiView> statusItemRfiViews;

  public StatusItemView(String status, String statusExplanation, String processingLabel, String processingDescription, List<StatusItemRfiView> statusItemRfiViews) {
    this.status = status;
    this.statusExplanation = statusExplanation;
    this.processLabel = processingLabel;
    this.processDescription = processingDescription;
    this.statusItemRfiViews = statusItemRfiViews;
  }

  public String getStatus() {
    return status;
  }

  public String getStatusExplanation() {
    return statusExplanation;
  }

  public String getProcessLabel() {
    return processLabel;
  }

  public String getProcessDescription() {
    return processDescription;
  }

  public List<StatusItemRfiView> getStatusItemRfiViews() {
    return statusItemRfiViews;
  }
}
