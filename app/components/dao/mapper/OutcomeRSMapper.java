package components.dao.mapper;

import components.dao.helper.LongMapper;
import components.util.JsonUtil;
import models.Outcome;
import models.OutcomeDocument;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class OutcomeRSMapper implements ResultSetMapper<Outcome> {

  @Override
  public Outcome map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String caseReference = r.getString("case_reference");
    String createdByUserId = r.getString("created_by_user_id");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String documentsJson = r.getString("documents");
    List<OutcomeDocument> outcomeDocuments = JsonUtil.convertJsonToOutcomeDocuments(documentsJson);
    return new Outcome(id,
        caseReference,
        createdByUserId,
        recipientUserIds,
        createdTimestamp,
        outcomeDocuments);
  }

}
