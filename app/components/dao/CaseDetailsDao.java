package components.dao;

import models.CaseDetails;

import java.util.List;

public interface CaseDetailsDao {

  void insert(CaseDetails caseDetails);

  boolean hasCase(String appId);

  List<CaseDetails> getCaseDetailsListByAppIds(List<String> appIds);

  void deleteAllCaseDetails();

  void deleteCaseDetails(String caseReference);

}
