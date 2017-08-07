package components.service;

public class PersonServiceMock implements PersonService {

  @Override
  public String getPerson(String personId) {
    return personId;
  }

}
