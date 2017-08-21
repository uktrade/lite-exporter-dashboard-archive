package models;

public class User {

  private final String id;
  private final String name;
  private final String email;
  private final String telephone;

  public User(String id, String name, String email, String telephone) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.telephone = telephone;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getTelephone() {
    return telephone;
  }

}
