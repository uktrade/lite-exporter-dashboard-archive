package models.view;

import java.util.List;

public class StatusItemView {

  private final String status;
  private final String statusExplanation;
  private final String processLabel;
  private final String processDescription;
  private final List<String> rfiList;

  public StatusItemView(String status, String statusExplanation, String processingLabel, String processingDescription, List<String> rfiList) {
    this.status = status;
    this.statusExplanation = statusExplanation;
    this.processLabel = processingLabel;
    this.processDescription = processingDescription;
    this.rfiList = rfiList;
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

  public List<String> getRfiList() {
    return rfiList;
  }
}
