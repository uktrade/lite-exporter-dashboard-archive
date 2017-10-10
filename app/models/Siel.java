package models;

import java.util.List;
import models.enums.SielStatus;

public class Siel {

  private final String id;
  private final String customerId;
  private final String applicantReference;
  private final String caseReference;
  private final Long issueTimestamp;
  private final Long expiryTimestamp;
  private final SielStatus sielStatus;
  private final String siteId;
  private final List<String> destinationList;

  public Siel(String id, String customerId, String applicantReference, String caseReference, Long issueTimestamp, Long expiryTimestamp, SielStatus sielStatus, String siteId, List<String> destinationList) {
    this.id = id;
    this.customerId = customerId;
    this.applicantReference = applicantReference;
    this.caseReference = caseReference;
    this.issueTimestamp = issueTimestamp;
    this.expiryTimestamp = expiryTimestamp;
    this.sielStatus = sielStatus;
    this.siteId = siteId;
    this.destinationList = destinationList;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public Long getIssueTimestamp() {
    return issueTimestamp;
  }

  public Long getExpiryTimestamp() {
    return expiryTimestamp;
  }

  public SielStatus getSielStatus() {
    return sielStatus;
  }

  public String getSiteId() {
    return siteId;
  }

  public List<String> getDestinationList() {
    return destinationList;
  }

}
