package components.dao;

import java.util.List;
import models.AmendmentRequest;

public interface AmendmentRequestDao {

  List<AmendmentRequest> getAmendmentRequests(String appId);

  List<AmendmentRequest> getAmendmentRequests(List<String> appIds);

  void insertAmendmentRequest(AmendmentRequest amendmentRequest);

  void deleteAllAmendmentRequests();

  void deleteAmendmentRequestsByAppId(String appId);

}
