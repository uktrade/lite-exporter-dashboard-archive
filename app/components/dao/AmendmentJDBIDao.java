package components.dao;

import models.Amendment;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

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

}