package components.dao;

import components.util.JsonUtil;
import models.Application;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ApplicationRSMapper implements ResultSetMapper<Application> {

  @Override
  public Application map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String appId = r.getString("app_id");
    String companyId = r.getString("company_id");
    String createdBy = r.getString("created_by");
    Long createdTimestamp = LongMapper.getLong(r, "created_timestamp");
    Long submittedTimestamp = LongMapper.getLong(r, "submitted_timestamp");
    String applicantReference = r.getString("applicant_reference");
    String destinationListJson = r.getString("destination_list");
    List<String> destinationList = JsonUtil.convertJsonToList(destinationListJson);
    String caseReference = r.getString("case_reference");
    String caseOfficerId = r.getString("case_officer_id");
    return new Application(appId, companyId, createdBy, createdTimestamp, submittedTimestamp, destinationList, applicantReference, caseReference, caseOfficerId);
  }

}
