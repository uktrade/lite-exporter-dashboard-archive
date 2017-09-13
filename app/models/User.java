package models;

public class User {

  private final String id;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String telephone;

  public User(String id, String firstName, String lastName, String email, String telephone) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.telephone = telephone;
  }

  public String getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getTelephone() {
    return telephone;
  }
}
