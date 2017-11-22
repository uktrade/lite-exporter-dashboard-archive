package components.dao;

import java.util.List;
import models.Outcome;

public interface OutcomeDao {

  List<Outcome> getOutcomes(List<String> caseReferences);

  void insertOutcome(Outcome outcome);

  void deleteAllOutcomes();

  void deleteOutcome(String caseReference);

}
