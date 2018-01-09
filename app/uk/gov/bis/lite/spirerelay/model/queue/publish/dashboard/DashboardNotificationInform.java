package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardNotificationInform implements SpireToDashboardMessage {

  @NotEmpty
  private String id;

  @NotEmpty
  private String appId;

  @NotEmpty
  private String caseRef;

  @NotEmpty
  private String createdByUserId;

  private List<String> recipientUserIds = Collections.emptyList();

  @NotNull
  private Long createdTimestamp;

  @NotNull
  private DashboardMessageDocument document;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

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

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public void setCreatedByUserId(String createdByUserId) {
    this.createdByUserId = createdByUserId;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public void setRecipientUserIds(List<String> recipientUserIds) {
    this.recipientUserIds = recipientUserIds;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Long createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public DashboardMessageDocument getDocument() {
    return document;
  }

  public void setDocument(DashboardMessageDocument document) {
    this.document = document;
  }

}
