package models;

import com.fasterxml.jackson.annotation.JsonProperty;

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
  private final String caseOfficerId;
  private final String siteId;

  public Application(@JsonProperty("id") String id,
                     @JsonProperty("customerId") String customerId,
                     @JsonProperty("createdByUserId") String createdByUserId,
                     @JsonProperty("createdTimestamp") Long createdTimestamp,
                     @JsonProperty("submittedTimestamp") Long submittedTimestamp,
                     @JsonProperty("consigneeCountries") List<String> consigneeCountries,
                     @JsonProperty("endUserCountries") List<String> endUserCountries,
                     @JsonProperty("applicantReference") String applicantReference,
                     @JsonProperty("caseOfficerId") String caseOfficerId,
                     @JsonProperty("siteId") String siteId) {
    this.id = id;
    this.customerId = customerId;
    this.createdByUserId = createdByUserId;
    this.createdTimestamp = createdTimestamp;
    this.submittedTimestamp = submittedTimestamp;
    this.consigneeCountries = consigneeCountries;
    this.endUserCountries = endUserCountries;
    this.applicantReference = applicantReference;
    this.caseOfficerId = caseOfficerId;
    this.siteId = siteId;
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

  public String getCaseOfficerId() {
    return caseOfficerId;
  }

  public String getSiteId() {
    return siteId;
  }

}
