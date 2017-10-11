package models;

import java.util.List;

public class Application {

  private final String id;
  private final String customerId;
  private final String createdByUserId;
  private final Long createdTimestamp;
  private final Long submittedTimestamp;
  private final List<String> consigneeCountries;
  private final List<String> endUserCountries;
  private final String applicantReference;
  private final String caseReference;
  private final String caseOfficerId;

  public Application(String id, String customerId, String createdByUserId, Long createdTimestamp, Long submittedTimestamp, List<String> consigneeCountries, List<String> endUserCountries, String applicantReference, String caseReference, String caseOfficerId) {
    this.id = id;
    this.customerId = customerId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.submittedTimestamp = submittedTimestamp;
    this.consigneeCountries = consigneeCountries;
    this.endUserCountries = endUserCountries;
    this.applicantReference = applicantReference;
    this.caseReference = caseReference;
    this.caseOfficerId = caseOfficerId;
  }

  public String getId() {
    return id;
  }

  public String getCustomerId() {
    return customerId;
  }

  public String getCreatedByUserId() {
    return createdByUserId;
  }

  public Long getCreatedTimestamp() {
    return createdTimestamp;
  }

  public Long getSubmittedTimestamp() {
    return submittedTimestamp;
  }

  public List<String> getConsigneeCountries() {
    return consigneeCountries;
  }

  public List<String> getEndUserCountries() {
    return endUserCountries;
  }

  public String getApplicantReference() {
    return applicantReference;
  }

  public String getCaseReference() {
    return caseReference;
  }

  public String getCaseOfficerId() {
    return caseOfficerId;
  }

}
