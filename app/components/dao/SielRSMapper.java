package components.dao;

import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.Siel;
import models.enums.SielStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class SielRSMapper implements ResultSetMapper<Siel> {

  @Override
  public Siel map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String customerId = r.getString("customer_id");
    String applicantReference = r.getString("applicant_reference");
    String caseReference = r.getString("case_reference");
    Long issueTimestamp = LongMapper.getLong(r, "issue_timestamp");
    Long expiryTimestamp = LongMapper.getLong(r, "expiry_timestamp");
    SielStatus sielStatus = SielStatus.valueOf(r.getString("status"));
    String siteId = r.getString("site_id");
    String destinationListJson = r.getString("destination_list");
    List<String> destinationList = JsonUtil.convertJsonToList(destinationListJson);
    return new Siel(id, customerId, applicantReference, caseReference, issueTimestamp, expiryTimestamp, sielStatus, siteId, destinationList);

  }

}
