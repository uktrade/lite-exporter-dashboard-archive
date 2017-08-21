package models.view;

public class OfficerView {

  private final String name;
  private final String telephone;
  private final String email;

  public OfficerView(String name, String telephone, String email) {
    this.name = name;
    this.telephone = telephone;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getTelephone() {
    return telephone;
  }

  public String getEmail() {
    return email;
  }

}
