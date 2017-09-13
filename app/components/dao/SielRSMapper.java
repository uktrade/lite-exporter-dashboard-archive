package components.dao;

import components.util.JsonUtil;
import models.Siel;
import models.enums.SielStatus;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SielRSMapper implements ResultSetMapper<Siel> {

  @Override
  public Siel map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String sielId = r.getString("siel_id");
    String companyId = r.getString("company_id");
    String applicantReference = r.getString("applicant_reference");
    String caseReference = r.getString("case_reference");
    Long issueTimestamp = LongMapper.getLong(r, "issue_timestamp");
    Long expiryTimestamp = LongMapper.getLong(r, "expiry_timestamp");
    SielStatus sielStatus = SielStatus.valueOf(r.getString("status"));
    String siteId = r.getString("site_id");
    String destinationListJson = r.getString("destination_list");
    List<String> destinationList = JsonUtil.convertJsonToList(destinationListJson);
    return new Siel(sielId, companyId, applicantReference, caseReference, issueTimestamp, expiryTimestamp, sielStatus, siteId, destinationList);

  }

}
