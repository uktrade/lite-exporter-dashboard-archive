package components.dao;

import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.Application;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class ApplicationRSMapper implements ResultSetMapper<Application> {

  @Override
  public Application map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String companyId = r.getString("customer_id");
    String createdBy = r.getString("created_by_user_id");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    Long submittedTimestamp = LongMapper.getLong(r, "submitted_timestamp");
    String applicantReference = r.getString("applicant_reference");
    String destinationListJson = r.getString("destination_list");
    List<String> destinationList = JsonUtil.convertJsonToList(destinationListJson);
    String caseReference = r.getString("case_reference");
    String caseOfficerId = r.getString("case_officer_id");
    return new Application(id, companyId, createdBy, createdTimestamp, submittedTimestamp, destinationList, applicantReference, caseReference, caseOfficerId);
  }

}
