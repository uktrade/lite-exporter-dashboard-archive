package models;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

import javax.validation.constraints.NotNull;

public class Outcome implements RecipientMessage {

  @NotBlank
  private final String id;

  @NotBlank
  private final String caseReference;

  @NotBlank
  private final String createdByUserId;

  @NotNull
  private final List<String> recipientUserIds;

  @NotNull
  private final Long createdTimestamp;

  @NotEmpty
  private final List<OutcomeDocument> outcomeDocuments;

  public Outcome(String id, String caseReference, String createdByUserId, List<String> recipientUserIds, Long createdTimestamp, List<OutcomeDocument> outcomeDocuments) {
    this.id = id;
    this.caseReference = caseReference;
    this.createdByUserId = createdByUserId;
    this.recipientUserIds = recipientUserIds;
    this.createdTimestamp = createdTimestamp;
    this.outcomeDocuments = outcomeDocuments;
  }

  public String getId() {
    return id;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public List<String> getRecipientUserIds() {
    return recipientUserIds;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public List<OutcomeDocument> getOutcomeDocuments() {
    return outcomeDocuments;
  }

}
