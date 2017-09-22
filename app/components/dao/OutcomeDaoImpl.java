package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.Outcome;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.List;

public class OutcomeDaoImpl implements OutcomeDao {

  private final DBI dbi;

  @Inject
  public OutcomeDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Outcome> getOutcomes(String appId) {
    try (final Handle handle = dbi.open()) {
      OutcomeJDBIDao outcomeJDBIDao = handle.attach(OutcomeJDBIDao.class);
      return outcomeJDBIDao.getOutcomes(appId);
    }
  }

  @Override
  public void insertOutcome(Outcome outcome) {
    try (final Handle handle = dbi.open()) {
      OutcomeJDBIDao outcomeJDBIDao = handle.attach(OutcomeJDBIDao.class);
      outcomeJDBIDao.insertOutcome(outcome.getId(),
          outcome.getAppId(),
          outcome.getCreatedByUserId(),
          JsonUtil.convertListToJson(outcome.getRecipientUserIds()),
          outcome.getCreatedTimestamp(),
          JsonUtil.convertListToJson(outcome.getDocuments()));
    }
  }

  @Override
  public void deleteAllOutcomes() {
    try (final Handle handle = dbi.open()) {
      OutcomeJDBIDao outcomeJDBIDao = handle.attach(OutcomeJDBIDao.class);
      outcomeJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteOutcomesByAppIds(List<String> appIds) {
    if (!appIds.isEmpty()) {
      try (final Handle handle = dbi.open()) {
        OutcomeJDBIDao outcomeJDBIDao = handle.attach(OutcomeJDBIDao.class);
        outcomeJDBIDao.deleteOutcomesByAppIds(appIds);
      }
    }
  }

}
