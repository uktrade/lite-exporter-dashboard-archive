package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.Siel;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

public class SielDaoImpl implements SielDao {

  private final DBI dbi;

  @Inject
  public SielDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Siel> getSiels(List<String> customerIds) {
    if (customerIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        SielJDBIDao sielJDBIDao = handle.attach(SielJDBIDao.class);
        return sielJDBIDao.getSiels(customerIds);
      }
    }
  }

  @Override
  public Siel getSiel(String caseReference) {
    try (final Handle handle = dbi.open()) {
      SielJDBIDao sielJDBIDao = handle.attach(SielJDBIDao.class);
      return sielJDBIDao.getSiel(caseReference);
    }
  }

  @Override
  public void insert(Siel siel) {
    try (final Handle handle = dbi.open()) {
      SielJDBIDao sielJDBIDao = handle.attach(SielJDBIDao.class);
      sielJDBIDao.insertSiel(siel.getSielId(),
          siel.getCompanyId(),
          siel.getApplicantReference(),
          siel.getCaseReference(),
          siel.getIssueTimestamp(),
          siel.getExpiryTimestamp(),
          siel.getSielStatus(),
          siel.getSiteId(),
          JsonUtil.convertListToJson(siel.getDestinationList()));
    }
  }

  @Override
  public void deleteAllSiels() {
    try (final Handle handle = dbi.open()) {
      SielJDBIDao sielJDBIDao = handle.attach(SielJDBIDao.class);
      sielJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteSielsByCustomerId(String customerId) {
    try (final Handle handle = dbi.open()) {
      SielJDBIDao sielJDBIDao = handle.attach(SielJDBIDao.class);
      sielJDBIDao.deleteSielsByCustomerId(customerId);
    }
  }

}
