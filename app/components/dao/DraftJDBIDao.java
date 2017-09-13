package components.dao;

import models.Draft;
import models.enums.DraftType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface DraftJDBIDao {

  @Mapper(DraftRSMapper.class)
  @SqlQuery("SELECT * FROM DRAFT WHERE RELATED_ID = :relatedId AND DRAFT_TYPE = :draftType")
  Draft getDraft(@Bind("relatedId") String relatedId, @Bind("draftType") DraftType draftType);

  @SqlUpdate("INSERT INTO DRAFT (RELATED_ID, DRAFT_TYPE, ATTACHMENTS) VALUES (:relatedId, :draftType, :attachments)")
  void insertDraft(@Bind("relatedId") String relatedId, @Bind("draftType") DraftType draftType, @Bind("attachments") String attachments);

  @SqlUpdate("DELETE FROM DRAFT WHERE RELATED_ID = :relatedId AND DRAFT_TYPE = :draftType")
  void deleteDraft(@Bind("relatedId") String relatedId, @Bind("draftType") DraftType draftType);

  @SqlUpdate("DELETE FROM DRAFT")
  void truncateTable();

}
