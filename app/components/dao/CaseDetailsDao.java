package components.dao;

import java.util.List;
import models.CaseDetails;

public interface CaseDetailsDao {

  void insert(CaseDetails caseDetails);

  List<CaseDetails> getCaseDetailsListByAppId(String appId);

  List<CaseDetails> getCaseDetailsListByAppIds(List<String> appIds);

  void deleteAllCaseDetails();

  void deleteCaseDetails(String caseReference);

}
