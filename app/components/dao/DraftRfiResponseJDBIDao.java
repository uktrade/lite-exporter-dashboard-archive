package components.dao;

import models.DraftRfiResponse;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface DraftRfiResponseJDBIDao {

  @Mapper(DraftRfiResponseRSMapper.class)
  @SqlQuery("SELECT * FROM DRAFT_RFI_RESPONSE WHERE RFI_ID = :rfiId")
  DraftRfiResponse getDraftRfiResponse(@Bind("rfiId") String rfiId);

  @SqlUpdate("INSERT INTO DRAFT_RFI_RESPONSE (RFI_ID, ATTACHMENTS) VALUES (:rfiId, :attachments)")
  void insertDraftRfiResponse(@Bind("rfiId") String rfiId, @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM DRAFT_RFI_RESPONSE WHERE RFI_ID = :rfiId")
  void delete(@Bind("rfiId") String rfiId);

  @SqlUpdate("DELETE FROM DRAFT_RFI_RESPONSE")
  void truncateTable();

}
