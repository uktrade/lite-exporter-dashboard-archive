package components.dao;

import models.Amendment;

import java.util.List;

public interface AmendmentDao {

  List<Amendment> getAmendments(String appId);

  List<Amendment> getAmendments(List<String> appIds);

  void insertAmendment(Amendment amendment);

  void deleteAllAmendments();

  void deleteAmendmentsByAppId(String appId);

}
