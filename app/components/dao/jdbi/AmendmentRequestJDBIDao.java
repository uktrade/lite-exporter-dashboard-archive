package components.dao.jdbi;

import components.dao.mapper.AmendmentRequestRSMapper;
import java.util.List;
import models.AmendmentRequest;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

@UseStringTemplate3StatementLocator
public interface AmendmentRequestJDBIDao {

  @RegisterMapper(AmendmentRequestRSMapper.class)
  @SqlQuery("SELECT * FROM AMENDMENT WHERE APP_ID in (<appIds>)")
  List<AmendmentRequest> getAmendmentRequests(@BindIn("appIds") List<String> appIds);

  @SqlUpdate("INSERT INTO AMENDMENT (ID,  APP_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, MESSAGE,  ATTACHMENTS) VALUES" +
      "                            (:id, :appId, :createdByUserId,   :createdTimestamp, :message, :attachments)")
  void insertAmendmentRequest(@Bind("id") String id,
                              @Bind("appId") String appId,
                              @Bind("createdByUserId") String createdByUserId,
                              @Bind("createdTimestamp") Long createdTimestamp,
                              @Bind("message") String message,
                              @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM AMENDMENT")
  void truncateTable();

  @SqlUpdate("DELETE FROM AMENDMENT WHERE APP_ID = :appId")
  void deleteAmendmentRequestsByAppId(@Bind("appId") String appId);

}
