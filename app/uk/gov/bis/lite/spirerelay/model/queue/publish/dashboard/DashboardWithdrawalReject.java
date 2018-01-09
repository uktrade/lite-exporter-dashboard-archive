package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardWithdrawalReject implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String createdByUserId;

  @NotEmpty
  private String message;

  private List<String> recipientUserIds = Collections.emptyList();

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String rejectedByUserId) {
    this.createdByUserId = rejectedByUserId;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public void setRecipientUserIds(List<String> recipientUserIds) {
    this.recipientUserIds = recipientUserIds;
  }
}
