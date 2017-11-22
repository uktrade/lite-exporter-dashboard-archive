package components.dao.impl;

import com.google.inject.Inject;
import components.dao.RfiReplyDao;
import components.dao.jdbi.RfiReplyJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.List;
import models.RfiReply;
import org.skife.jdbi.v2.DBI;

public class RfiReplyDaoImpl implements RfiReplyDao {

  private final RfiReplyJDBIDao rfiReplyJDBIDao;

  @Inject
  public RfiReplyDaoImpl(DBI dbi) {
    this.rfiReplyJDBIDao = dbi.onDemand(RfiReplyJDBIDao.class);
  }

  @Override
  public List<RfiReply> getRfiReplies(List<String> rfiIds) {
    if (rfiIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return rfiReplyJDBIDao.getRfiReply(rfiIds);
    }
  }

  @Override
  public RfiReply getRfiReply(String rfiId) {
    return rfiReplyJDBIDao.getRfiReply(rfiId);
  }

  @Override
  public void insertRfiReply(RfiReply rfiReply) {
    rfiReplyJDBIDao.insertRfiReply(
        rfiReply.getId(),
        rfiReply.getRfiId(),
        rfiReply.getCreatedByUserId(),
        rfiReply.getCreatedTimestamp(),
        rfiReply.getMessage(),
        JsonUtil.convertListToJson(rfiReply.getAttachments()));
  }

  @Override
  public void deleteAllRfiReplies() {
    rfiReplyJDBIDao.truncateTable();
  }

  @Override
  public void deleteRfiRepliesByRfiId(String rfiId) {
    rfiReplyJDBIDao.deleteRfiRepliesByRfiId(rfiId);
  }

}
