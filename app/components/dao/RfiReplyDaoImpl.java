package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import models.RfiReply;

import java.util.ArrayList;
import java.util.List;

public class RfiReplyDaoImpl implements RfiReplyDao {

  private final DBI dbi;

  @Inject
  public RfiReplyDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<RfiReply> getRfiReplies(List<String> rfiIds) {
    if (rfiIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        RfiReplyJDBIDao rfiReplyJDBIDao = handle.attach(RfiReplyJDBIDao.class);
        return rfiReplyJDBIDao.getRfiReply(rfiIds);
      }
    }
  }

  @Override
  public RfiReply getRfiReply(String rfiId) {
    try (final Handle handle = dbi.open()) {
      RfiReplyJDBIDao rfiReplyJDBIDao = handle.attach(RfiReplyJDBIDao.class);
      return rfiReplyJDBIDao.getRfiReply(rfiId);
    }
  }

  @Override
  public void insertRfiReply(RfiReply rfiReply) {
    try (final Handle handle = dbi.open()) {
      RfiReplyJDBIDao rfiReplyJDBIDao = handle.attach(RfiReplyJDBIDao.class);
      rfiReplyJDBIDao.insertRfiReply(
          rfiReply.getId(),
          rfiReply.getRfiId(),
          rfiReply.getCreatedByUserId(),
          rfiReply.getCreatedTimestamp(),
          rfiReply.getMessage(),
          JsonUtil.convertListToJson(rfiReply.getAttachments()));
    }
  }

  @Override
  public void deleteAllRfiReplies() {
    try (final Handle handle = dbi.open()) {
      RfiReplyJDBIDao rfiReplyJDBIDao = handle.attach(RfiReplyJDBIDao.class);
      rfiReplyJDBIDao.truncateTable();
    }
  }

  @Override
  public void deleteRfiRepliesByRfiId(String rfiId) {
    try (final Handle handle = dbi.open()) {
      RfiReplyJDBIDao rfiReplyJDBIDao = handle.attach(RfiReplyJDBIDao.class);
      rfiReplyJDBIDao.deleteRfiRepliesByRfiId(rfiId);
    }
  }

}
