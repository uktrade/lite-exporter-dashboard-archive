package components.dao;

import models.Amendment;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface AmendmentJDBIDao {

  @RegisterMapper(AmendmentRSMapper.class)
  @SqlQuery("SELECT FROM AMENDMENT WHERE APP_ID = :appId")
  List<Amendment> getAmendments(@Bind("appId") String appId);

  @SqlUpdate("INSERT INTO AMENDMENT ( AMENDMENT_ID, APP_ID, SENT_TIMESTAMP, SENT_BY, MESSAGE,  ATTACHMENTS) VALUES" +
      "                             (:amendmentId, :appId, :sentTimestamp, :sentBy, :message, :attachments)")
  void insertAmendment(@Bind("amendmentId") String amendmentId,
                       @Bind("appId") String appId,
                       @Bind("sentTimestamp") Long sentTimestamp,
                       @Bind("sentBy") String sentBy,
                       @Bind("message") String message,
                       @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM AMENDMENT")
  void truncateTable();

  @SqlUpdate("DELETE FROM AMENDMENT WHERE APP_ID in (<appIds>)")
  void deleteAmendmentsByAppIds(@BindIn("appIds") List<String> appIds);

}
