package models.view;

import java.util.List;

public class OutcomeView {

  private final String issuedOn;
  private final String voidedOn;
  private final List<OutcomeDocumentView> outcomeDocumentViews;
  private final boolean showNewIndicator;

  public OutcomeView(String issuedOn, String voidedOn, List<OutcomeDocumentView> outcomeDocumentViews, boolean showNewIndicator) {
    this.issuedOn = issuedOn;
    this.voidedOn = voidedOn;
    this.outcomeDocumentViews = outcomeDocumentViews;
    this.showNewIndicator = showNewIndicator;
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

  public boolean isShowNewIndicator() {
    return showNewIndicator;
  }

}
