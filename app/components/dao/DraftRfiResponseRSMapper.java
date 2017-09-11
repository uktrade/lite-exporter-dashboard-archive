package components.dao;

import components.util.JsonUtil;
import models.DraftRfiResponse;
import models.File;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DraftRfiResponseRSMapper implements ResultSetMapper<DraftRfiResponse> {

  @Override
  public DraftRfiResponse map(int index, ResultSet r, StatementContext ctx) throws SQLException {
    String rfiId = r.getString("rfi_id");
    String attachmentsJson = r.getString("attachments");
    List<File> attachments = JsonUtil.convertJsonToFiles(attachmentsJson);
    return new DraftRfiResponse(rfiId, attachments);
  }

}
