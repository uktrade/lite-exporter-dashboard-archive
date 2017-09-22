package models.view;

import java.util.List;

public class OutcomeView {

  private String issuedOn;

  private String voidedOn;

  private List<String> documents;

  public OutcomeView(String issuedOn, String voidedOn, List<String> documents) {
    this.issuedOn = issuedOn;
    this.voidedOn = voidedOn;
    this.documents = documents;
  }

  public String getIssuedOn() {
    return issuedOn;
  }

  public String getVoidedOn() {
    return voidedOn;
  }

  public List<String> getDocuments() {
    return documents;
  }

}
