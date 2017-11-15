package components.dao.jdbi;

import components.dao.mapper.ReadRSMapper;
import java.util.List;
import models.Read;
import models.enums.ReadType;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface ReadJDBIDao {

  @Mapper(ReadRSMapper.class)
  @SqlQuery("SELECT * FROM READ WHERE CREATED_BY_USER_ID = :createdByUserId")
  List<Read> getReadList(@Bind("createdByUserId") String createdByUserId);

  @SqlUpdate("INSERT INTO READ (ID,  RELATED_ID, READ_TYPE, CREATED_BY_USER_ID) VALUES "
      + "                     (:id, :relatedId, :readType, :createdByUserId)")
  void insertRead(@Bind("id") String id,
                  @Bind("relatedId") String relatedId,
                  @Bind("readType") ReadType readType,
                  @Bind("createdByUserId") String createdByUserId);

  @SqlUpdate("DELETE FROM READ WHERE CREATED_BY_USER_ID = :userId")
  void deleteAllReadDataByUserId(@Bind("userId") String userId);

  @SqlUpdate("DELETE FROM READ")
  void truncateTable();

}
