package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardRfiCreate implements SpireToDashboardMessage {

  @NotEmpty
  private String appId;

  @NotEmpty
  private String caseRef;

  @NotEmpty
  private List<String> recipientUserIds = Collections.emptyList();;

  @NotEmpty
  private String id;

  @NotEmpty
  private String message;

  @NotEmpty
  private String createdByUserId;

  @NotEmpty
  private Long createdTimestamp;

  @NotEmpty
  private Long deadlineTimestamp;

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getCaseRef() {
    return caseRef;
  }

  public void setCaseRef(String caseRef) {
    this.caseRef = caseRef;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String sentByUserId) {
    this.createdByUserId = sentByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public Long getDeadlineTimestamp() {
    return deadlineTimestamp;
  }

  public void setDeadlineTimestamp(Long deadlineTimestamp) {
    this.deadlineTimestamp = deadlineTimestamp;
  }

  public void setRecipientUserIds(List<String> recipientUserIds) {
    this.recipientUserIds = recipientUserIds;
  }

}
