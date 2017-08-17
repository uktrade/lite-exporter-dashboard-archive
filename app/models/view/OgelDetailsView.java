package models.view;

import uk.gov.bis.lite.ogel.api.view.OgelFullView;

public class OgelDetailsView {

  private final String registrationReference;
  private final String name;
  private final String link;
  private final OgelFullView.OgelConditionSummary ogelConditionSummary;

  public OgelDetailsView(String registrationReference, String name, String link, OgelFullView.OgelConditionSummary ogelConditionSummary) {
    this.registrationReference = registrationReference;
    this.name = name;
    this.link = link;
    this.ogelConditionSummary = ogelConditionSummary;
  }

  public String getRegistrationReference() {
    return registrationReference;
  }

  public String getName() {
    return name;
  }

  public String getLink() {
    return link;
  }

  public OgelFullView.OgelConditionSummary getOgelConditionSummary() {
    return ogelConditionSummary;
  }
}
