package components.dao.impl;

import components.dao.CaseDetailsDao;
import components.dao.jdbi.CaseDetailsJDBIDao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import models.CaseDetails;
import org.skife.jdbi.v2.DBI;

public class CaseDetailsDaoImpl implements CaseDetailsDao {

  private final CaseDetailsJDBIDao caseDetailsJDBIDao;

  @Inject
  public CaseDetailsDaoImpl(DBI dbi) {
    this.caseDetailsJDBIDao = dbi.onDemand(CaseDetailsJDBIDao.class);
  }

  @Override
  public void insert(CaseDetails caseDetails) {
    caseDetailsJDBIDao.insert(caseDetails.getAppId(),
        caseDetails.getCaseReference(),
        caseDetails.getCreatedByUserId(),
        caseDetails.getCreatedTimestamp());
  }

  @Override
  public List<CaseDetails> getCaseDetailsListByAppId(String appId) {
    return getCaseDetailsListByAppIds(Collections.singletonList(appId));
  }

  @Override
  public List<CaseDetails> getCaseDetailsListByAppIds(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return caseDetailsJDBIDao.getCaseDetailsListByAppIds(appIds);
    }
  }

  @Override
  public void deleteAllCaseDetails() {
    caseDetailsJDBIDao.truncateTable();
  }

  @Override
  public void deleteCaseDetails(String caseReference) {
    caseDetailsJDBIDao.deleteCaseDetails(caseReference);
  }

}
