package components.dao.jdbi;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public interface BacklogJDBIDao {

  @SqlUpdate("INSERT INTO BACKLOG ( CREATED_TIMESTAMP, ROUTING_KEY, MESSAGE) "
      + "                  VALUES (:createdTimestamp, :routingKey, :message)")
  void insert(@Bind("createdTimestamp") long createdTimestamp,
              @Bind("routingKey") String routingKey,
              @Bind("message") String message);

  @SqlUpdate("TRUNCATE TABLE BACKLOG")
  void truncateTable();

}
