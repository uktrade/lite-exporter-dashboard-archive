package models.view;

import java.util.List;

public class OutcomeView {

  private String issuedOn;

  private String voidedOn;

  private List<OutcomeDocumentView> outcomeDocumentViews;

  public OutcomeView(String issuedOn, String voidedOn, List<OutcomeDocumentView> outcomeDocumentViews) {
    this.issuedOn = issuedOn;
    this.voidedOn = voidedOn;
    this.outcomeDocumentViews = outcomeDocumentViews;
  }

  public String getIssuedOn() {
    return issuedOn;
  }

  public String getVoidedOn() {
    return voidedOn;
  }

  public List<OutcomeDocumentView> getOutcomeDocumentViews() {
    return outcomeDocumentViews;
  }

}
