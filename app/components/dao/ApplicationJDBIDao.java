package components.dao;

import models.Application;
import models.enums.ApplicationStatus;
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

  @SqlUpdate("INSERT INTO APPLICATION ( APP_ID, COMPANY_ID, STATUS,  APPLICANT_REFERENCE, DESTINATION_LIST, CASE_REFERENCE, CASE_OFFICER_ID) " +
      "                        VALUES (:appId, :companyId, :status, :applicantReference, :destinationList, :caseReference, :caseOfficerId) ")
  void insert(@Bind("appId") String appId,
              @Bind("companyId") String companyId,
              @Bind("status") ApplicationStatus applicationStatus,
              @Bind("applicantReference") String applicantReference,
              @Bind("destinationList") String destinationList,
              @Bind("caseReference") String caseReference,
              @Bind("caseOfficerId") String caseOfficerId);

  @SqlUpdate("DELETE FROM APPLICATION")
  void truncateTable();

}
