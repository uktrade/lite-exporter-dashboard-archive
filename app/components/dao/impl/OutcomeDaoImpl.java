package components.dao.impl;

import com.google.inject.Inject;
import components.dao.OutcomeDao;
import components.dao.jdbi.OutcomeJDBIDao;
import components.util.JsonUtil;
import models.Outcome;
import org.skife.jdbi.v2.DBI;

import java.util.ArrayList;
import java.util.List;

public class OutcomeDaoImpl implements OutcomeDao {

  private final OutcomeJDBIDao outcomeJDBIDao;

  @Inject
  public OutcomeDaoImpl(DBI dbi) {
    this.outcomeJDBIDao = dbi.onDemand(OutcomeJDBIDao.class);
  }

  @Override
  public List<Outcome> getOutcomes(List<String> caseReferences) {
    if (caseReferences.isEmpty()) {
      return new ArrayList<>();
    } else {
      return outcomeJDBIDao.getOutcomes(caseReferences);
    }
  }

  @Override
  public void insertOutcome(Outcome outcome) {
    outcomeJDBIDao.insertOutcome(outcome.getId(),
        outcome.getCaseReference(),
        outcome.getCreatedByUserId(),
        JsonUtil.convertListToJson(outcome.getRecipientUserIds()),
        outcome.getCreatedTimestamp(),
        JsonUtil.convertListToJson(outcome.getOutcomeDocuments()));
  }

  @Override
  public void deleteAllOutcomes() {
    outcomeJDBIDao.truncateTable();
  }

  @Override
  public void deleteOutcome(String caseReference) {
    outcomeJDBIDao.deleteOutcome(caseReference);
  }

}
