package components.dao.mapper;

import components.dao.helper.LongMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import models.CaseDetails;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CaseDetailsRSMapper implements ResultSetMapper<CaseDetails> {

  @Override
  public CaseDetails map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String appId = r.getString("app_id");
    String caseReference = r.getString("case_reference");
    String createdByUserId = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    return new CaseDetails(appId,
        caseReference,
        createdByUserId,
        createdTimestamp);
  }

}
