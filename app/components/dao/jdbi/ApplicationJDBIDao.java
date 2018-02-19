package components.dao.jdbi;

import components.dao.mapper.ApplicationRSMapper;
import models.Application;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface ApplicationJDBIDao {

  @Mapper(ApplicationRSMapper.class)
  @SqlQuery("SELECT * FROM APPLICATION WHERE CUSTOMER_ID in (<customerIds>) OR CREATED_BY_USER_ID = :userId")
  List<Application> getApplicationsByCustomerIdsAndUserId(@BindIn(value = "customerIds", onEmpty = BindIn.EmptyHandling.NULL) List<String> customerIds,
                                                          @Bind("userId") String userId);

  @Mapper(ApplicationRSMapper.class)
  @SqlQuery("SELECT * FROM APPLICATION WHERE ID = :id")
  Application getApplication(@Bind("id") String id);

  @SqlQuery("SELECT COUNT(*) FROM APPLICATION")
  long getApplicationCount();

  @SqlUpdate("UPDATE APPLICATION SET CASE_OFFICER_ID = :caseOfficerId WHERE ID = :id")
  void updateCaseOfficerId(@Bind("id") String id,
                           @Bind("caseOfficerId") String caseOfficerId);

  @SqlUpdate("UPDATE APPLICATION SET APPLICANT_REFERENCE = :applicantReference WHERE ID = :id")
  void updateApplicantReference(@Bind("id") String id,
                                @Bind("applicantReference") String applicantReference);

  @SqlUpdate("UPDATE APPLICATION SET CUSTOMER_ID = :customerId WHERE ID = :id")
  void updateCustomerId(@Bind("id") String id,
                        @Bind("customerId") String customerId);

  @SqlUpdate("UPDATE APPLICATION SET SITE_ID = :siteId WHERE ID = :id")
  void updateSiteId(@Bind("id") String id,
                    @Bind("siteId") String siteId);

  @SqlUpdate("UPDATE APPLICATION SET CONSIGNEE_COUNTRIES = :consigneeCountries, END_USER_COUNTRIES = :endUserCountries WHERE ID = :id")
  void updateCountries(@Bind("id") String id,
                       @Bind("consigneeCountries") String consigneeCountries,
                       @Bind("endUserCountries") String endUserCountries);

  @SqlUpdate("INSERT INTO APPLICATION ( ID,  CREATED_BY_USER_ID, CREATED_TIMESTAMP, CONSIGNEE_COUNTRIES, END_USER_COUNTRIES) " +
      "                        VALUES (:id, :createdByUserId,   :createdTimestamp, :consigneeCountries, :endUserCountries) ")
  void insert(@Bind("id") String id,
              @Bind("createdByUserId") String createdByUserId,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("consigneeCountries") String consigneeCountries,
              @Bind("endUserCountries") String endUserCountries);

  @SqlUpdate("DELETE FROM APPLICATION WHERE ID = :id")
  void deleteApplication(@Bind("id") String id);

  @SqlUpdate("DELETE FROM APPLICATION")
  void truncateTable();

}
