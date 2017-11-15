package components.dao.mapper;

import components.dao.helper.LongMapper;
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
    String consigneeCountriesJson = r.getString("consignee_countries");
    List<String> consigneeCountries = JsonUtil.convertJsonToList(consigneeCountriesJson);
    String endUserCountriesJson = r.getString("end_user_countries");
    List<String> endUserCountries = JsonUtil.convertJsonToList(endUserCountriesJson);
    String caseOfficerId = r.getString("case_officer_id");
    String siteId = r.getString("site_id");
    return new Application(id, companyId, createdBy, createdTimestamp, submittedTimestamp, consigneeCountries, endUserCountries, applicantReference, caseOfficerId, siteId);
  }

}
