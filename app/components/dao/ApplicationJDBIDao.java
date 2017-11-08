package components.dao;

import java.util.List;
import models.Application;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface ApplicationJDBIDao {

  @Mapper(ApplicationRSMapper.class)
  @SqlQuery("SELECT * FROM APPLICATION WHERE CUSTOMER_ID in (<customerIds>)")
  List<Application> getApplications(@BindIn("customerIds") List<String> customerIds);

  @Mapper(ApplicationRSMapper.class)
  @SqlQuery("SELECT * FROM APPLICATION WHERE ID = :id")
  Application getApplication(@Bind("id") String id);

  @SqlQuery("SELECT COUNT(*) FROM APPLICATION")
  long getApplicationCount();

  @SqlUpdate("INSERT INTO APPLICATION ( ID,  CUSTOMER_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, SUBMITTED_TIMESTAMP, CONSIGNEE_COUNTRIES, END_USER_COUNTRIES, APPLICANT_REFERENCE, CASE_REFERENCE, CASE_OFFICER_ID, SITE_ID) " +
      "                        VALUES (:id, :customerId, :createdByUserId,   :createdTimestamp, :submittedTimestamp, :consigneeCountries, :endUserCountries,  :applicantReference, :caseReference, :caseOfficerId,  :siteId) ")
  void insert(@Bind("id") String id,
              @Bind("customerId") String customerId,
              @Bind("createdByUserId") String createdByUserId,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("submittedTimestamp") Long submittedTimestamp,
              @Bind("consigneeCountries") String consigneeCountries,
              @Bind("endUserCountries") String endUserCountries,
              @Bind("applicantReference") String applicantReference,
              @Bind("caseReference") String caseReference,
              @Bind("caseOfficerId") String caseOfficerId,
              @Bind("siteId") String siteId);

  @SqlUpdate("DELETE FROM APPLICATION WHERE ID = :id")
  void delete(@Bind("id") String id);

  @SqlUpdate("DELETE FROM APPLICATION")
  void truncateTable();

}
