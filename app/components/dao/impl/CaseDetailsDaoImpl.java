package components.dao.impl;

import components.dao.CaseDetailsDao;
import components.dao.jdbi.ApplicationJDBIDao;
import components.dao.jdbi.CaseDetailsJDBIDao;
import components.util.JsonUtil;
import models.Application;
import models.CaseDetails;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class CaseDetailsDaoImpl implements CaseDetailsDao {

  private final DBI dbi;

  @Inject
  public CaseDetailsDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public void insert(CaseDetails caseDetails) {
    try (Handle handle = dbi.open()) {
      CaseDetailsJDBIDao caseDetailsJDBIDao = handle.attach(CaseDetailsJDBIDao.class);
      ApplicationJDBIDao applicationJDBIDao = handle.attach(ApplicationJDBIDao.class);
      handle.useTransaction((conn, status) -> {
        Application application = applicationJDBIDao.getApplication(caseDetails.getAppId());
        if (application == null) {
          applicationJDBIDao.insert(caseDetails.getAppId(),
              null,
              null,
              caseDetails.getCreatedTimestamp(),
              null,
              JsonUtil.convertListToJson(new ArrayList<>()),
              JsonUtil.convertListToJson(new ArrayList<>()),
              null,
              null,
              null);
        }
        caseDetailsJDBIDao.insert(caseDetails.getAppId(),
            caseDetails.getCaseReference(),
            caseDetails.getCreatedByUserId(),
            caseDetails.getCreatedTimestamp());
      });
    }
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
      try (Handle handle = dbi.open()) {
        CaseDetailsJDBIDao caseDetailsJDBIDao = handle.attach(CaseDetailsJDBIDao.class);
        return caseDetailsJDBIDao.getCaseDetailsListByAppIds(appIds);
      }
    }
  }

  @Override
  public void deleteAllCaseDetails() {
    try (Handle handle = dbi.open()) {
      CaseDetailsJDBIDao caseDetailsJDBIDao = handle.attach(CaseDetailsJDBIDao.class);
      caseDetailsJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteCaseDetails(String caseReference) {
    try (Handle handle = dbi.open()) {
      CaseDetailsJDBIDao caseDetailsJDBIDao = handle.attach(CaseDetailsJDBIDao.class);
      caseDetailsJDBIDao.deleteCaseDetails(caseReference);
    }
  }

}
