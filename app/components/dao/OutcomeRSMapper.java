package components.dao;

import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.Document;
import models.Outcome;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class OutcomeRSMapper implements ResultSetMapper<Outcome> {

  @Override
  public Outcome map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String appId = r.getString("app_id");
    String createdByUserId = r.getString("created_by_user_id");
    String recipientUserIdsJson = r.getString("recipient_user_ids");
    List<String> recipientUserIds = JsonUtil.convertJsonToList(recipientUserIdsJson);
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    String documentsJson = r.getString("documents");
    List<Document> documents = JsonUtil.convertJsonToDocuments(documentsJson);
    return new Outcome(id,
        appId,
        createdByUserId,
        recipientUserIds,
        createdTimestamp,
        documents);
  }

}
