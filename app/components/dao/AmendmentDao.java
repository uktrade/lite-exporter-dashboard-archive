package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.Amendment;

import java.util.List;

public interface AmendmentDao {

  List<Amendment> getAmendments(String appId);

  void insertAmendment(Amendment amendment);

  void deleteAllAmendments();

  void deleteAmendmentsByAppId(String appId);

}
