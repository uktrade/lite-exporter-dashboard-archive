package components.dao;

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
  @SqlQuery("SELECT * FROM APPLICATION WHERE COMPANY_ID in (<customerIds>)")
  List<Application> getApplications(@BindIn("customerIds") List<String> customerIds);

  @Mapper(ApplicationRSMapper.class)
  @SqlQuery("SELECT * FROM APPLICATION WHERE APP_ID = :appId")
  Application getApplication(@Bind("appId") String appId);

  @SqlQuery("SELECT COUNT(*) FROM APPLICATION")
  long getApplicationCount();

  @SqlUpdate("INSERT INTO APPLICATION ( APP_ID, COMPANY_ID, CREATED_BY, CREATED_TIMESTAMP, SUBMITTED_TIMESTAMP, DESTINATION_LIST, APPLICANT_REFERENCE, CASE_REFERENCE, CASE_OFFICER_ID) " +
      "                        VALUES (:appId, :companyId, :createdBy, :createdTimestamp, :submittedTimestamp, :destinationList, :applicantReference, :caseReference, :caseOfficerId) ")
  void insert(@Bind("appId") String appId,
              @Bind("companyId") String companyId,
              @Bind("createdBy") String createdBy,
              @Bind("createdTimestamp") Long createdTimestamp,
              @Bind("submittedTimestamp") Long submittedTimestamp,
              @Bind("destinationList") String destinationList,
              @Bind("applicantReference") String applicantReference,
              @Bind("caseReference") String caseReference,
              @Bind("caseOfficerId") String caseOfficerId);

  @SqlUpdate("DELETE FROM APPLICATION WHERE APP_ID = :appId")
  void delete(@Bind("appId") String appId);

  @SqlUpdate("DELETE FROM APPLICATION")
  void truncateTable();

}
