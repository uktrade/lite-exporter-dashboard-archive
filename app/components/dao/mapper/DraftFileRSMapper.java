package components.dao.mapper;

import models.DraftFile;
import models.enums.DraftType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DraftFileRSMapper implements ResultSetMapper<DraftFile> {

  @Override
  public DraftFile map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String relatedId = r.getString("related_id");
    DraftType draftType = DraftType.valueOf(r.getString("draft_type"));
    String filename = r.getString("filename");
    String url = r.getString("url");
    return new DraftFile(id, filename, url, relatedId, draftType);
  }

}
