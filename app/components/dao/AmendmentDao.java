package components.dao;

import models.Amendment;

import java.util.List;

public interface AmendmentDao {

  List<Amendment> getAmendments(String appId);

  void insertAmendment(Amendment amendment);

  void deleteAllAmendments();

  void deleteAmendmentsByAppIds(List<String> appIds);

}
