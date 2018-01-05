package components.dao.jdbi;

import components.dao.mapper.DraftFileRSMapper;
import models.DraftFile;
import models.enums.DraftType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

import java.util.List;

public interface DraftFileJDBIDao {

  @Mapper(DraftFileRSMapper.class)
  @SqlQuery("SELECT * FROM DRAFT_FILE WHERE RELATED_ID = :relatedId AND DRAFT_TYPE = :draftType")
  List<DraftFile> getDraftFiles(@Bind("relatedId") String relatedId, @Bind("draftType") DraftType draftType);

  @SqlUpdate("INSERT INTO DRAFT_FILE (ID, FILENAME, URL, RELATED_ID, DRAFT_TYPE) VALUES (:id, :filename, :url, :relatedId, :draftType)")
  void insertDraftFile(@Bind("id") String id,
                       @Bind("filename") String filename,
                       @Bind("url") String url,
                       @Bind("relatedId") String relatedId,
                       @Bind("draftType") DraftType draftType);

  @SqlUpdate("DELETE FROM DRAFT_FILE WHERE ID = :id AND RELATED_ID = :relatedId AND DRAFT_TYPE = :draftType")
  void deleteDraftFile(@Bind("id") String id,
                       @Bind("relatedId") String relatedId,
                       @Bind("draftType") DraftType draftType);

  @SqlUpdate("DELETE FROM DRAFT_FILE WHERE RELATED_ID = :relatedId AND DRAFT_TYPE = :draftType")
  void deleteDraftFiles(@Bind("relatedId") String relatedId,
                        @Bind("draftType") DraftType draftType);

  @SqlUpdate("DELETE FROM DRAFT_FILE")
  void truncateTable();

}
