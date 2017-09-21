package components.dao;

import uk.gov.bis.lite.exporterdashboard.api.RfiReply;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

import java.util.List;

@UseStringTemplate3StatementLocator
public interface RfiReplyJDBIDao {

  @Mapper(RfiReplyRSMapper.class)
  @SqlQuery("SELECT * FROM RFI_REPLY WHERE RFI_ID in (<rfiIds>)")
  List<RfiReply> getRfiReply(@BindIn("rfiIds") List<String> rfiIds);

  @Mapper(RfiReplyRSMapper.class)
  @SqlQuery("SELECT * FROM RFI_REPLY WHERE RFI_ID = :rfiId")
  RfiReply getRfiReply(@Bind("rfiId") String rfiId);

  @SqlUpdate("INSERT INTO RFI_REPLY (ID,  RFI_ID, CREATED_BY_USER_ID, CREATED_TIMESTAMP, MESSAGE,  ATTACHMENTS) VALUES " +
      "                            (:id, :rfiId, :createdByUserId,   :createdTimestamp, :message, :attachments)")
  void insertRfiReply(@Bind("id") String id,
                      @Bind("rfiId") String rfiId,
                      @Bind("createdByUserId") String createdByUserId,
                      @Bind("createdTimestamp") Long createdTimestamp,
                      @Bind("message") String message,
                      @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM RFI_REPLY")
  void truncateTable();

  @SqlUpdate("DELETE FROM RFI_REPLY WHERE RFI_ID in (<rfiIds>)")
  void deleteRfiRepliesByRfiIds(@BindIn("rfiIds") List<String> rfiIds);

}
