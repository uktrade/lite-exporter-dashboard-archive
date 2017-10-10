package components.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import models.Read;
import models.enums.ReadType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class ReadRSMapper implements ResultSetMapper<Read> {

  @Override
  public Read map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String relatedId = r.getString("related_id");
    ReadType readType = ReadType.valueOf(r.getString("read_type"));
    String createdByUserId = r.getString("created_by_user_id");
    return new Read(id, relatedId, readType, createdByUserId);
  }

}
