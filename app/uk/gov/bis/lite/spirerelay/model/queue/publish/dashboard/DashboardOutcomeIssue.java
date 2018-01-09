package uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardOutcomeIssue implements SpireToDashboardMessage {

  @NotEmpty
  private String id;

  @NotEmpty
  private String appId;

  @NotEmpty
  private String caseRef;

  @NotEmpty
  private String createdByUserId;

  private List<String> recipientUserIds = Collections.emptyList();;

  @NotNull
  private Long createdTimestamp;

  @NotEmpty
  private List<DashboardOutcomeDocument> documents = Collections.emptyList();;

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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public List<DashboardOutcomeDocument> getDocuments() {
    return documents;
  }

  public void setDocuments(List<DashboardOutcomeDocument> documents) {
    this.documents = documents;
  }
}
