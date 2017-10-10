package components.dao;

import models.Outcome;

import java.util.List;

public interface OutcomeDao {

  List<Outcome> getOutcomes(List<String> appIds);

  List<Outcome> getOutcomes(String appId);

  void insertOutcome(Outcome outcome);

  void deleteAllOutcomes();

  void deleteOutcomesByAppId(String appId);

}
