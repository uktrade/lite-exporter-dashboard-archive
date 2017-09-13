package components.dao;

import models.Siel;

import java.util.List;

public interface SielDao {

  List<Siel> getSiels(List<String> customerIds);

  Siel getSiel(String sielId);

  void insert(Siel siel);

  void deleteAllSiels();

}
