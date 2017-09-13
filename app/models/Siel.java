package models;

import models.enums.SielStatus;

import java.util.List;

public class Siel {

  private final String sielId;
  private final String companyId;
  private final String applicantReference;
  private final String caseReference;
  private final Long issueTimestamp;
  private final Long expiryTimestamp;
  private final SielStatus sielStatus;
  private final String siteId;
  private final List<String> destinationList;

  public Siel(String sielId,
              String companyId,
              String applicantReference,
              String caseReference,
              Long issueTimestamp,
              Long expiryTimestamp,
              SielStatus sielStatus,
              String siteId,
              List<String> destinationList) {
    this.sielId = sielId;
    this.companyId = companyId;
    this.applicantReference = applicantReference;
    this.caseReference = caseReference;
    this.issueTimestamp = issueTimestamp;
    this.expiryTimestamp = expiryTimestamp;
    this.sielStatus = sielStatus;
    this.siteId = siteId;
    this.destinationList = destinationList;
  }

  public String getSielId() {
    return sielId;
  }

  public String getCompanyId() {
    return companyId;
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
