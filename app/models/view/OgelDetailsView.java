package models.view;

public class OgelDetailsView {

  private final String registrationReference;
  private final String name;
  private final String link;
  private final String viewLetterLink;

  public OgelDetailsView(String registrationReference, String name, String link, String viewLetterLink) {
    this.registrationReference = registrationReference;
    this.name = name;
    this.link = link;
    this.viewLetterLink = viewLetterLink;
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

  public String getViewLetterLink() {
    return viewLetterLink;
  }

}
