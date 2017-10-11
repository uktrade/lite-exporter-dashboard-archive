package components.dao;

import components.util.JsonUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import models.Draft;
import models.enums.DraftType;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;
import models.File;

public class DraftRSMapper implements ResultSetMapper<Draft> {

  @Override
  public Draft map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String id = r.getString("id");
    String relatedId = r.getString("related_id");
    DraftType draftType = DraftType.valueOf(r.getString("draft_type"));
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);
    return new Draft(id, relatedId, draftType, attachments);
  }

}
