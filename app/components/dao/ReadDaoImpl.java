package components.dao;

import java.util.List;
import javax.inject.Inject;
import models.Read;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class ReadDaoImpl implements ReadDao {

  private final DBI dbi;

  @Inject
  public ReadDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<Read> getReadList(String userId) {
    try (Handle handle = dbi.open()) {
      ReadJDBIDao readJDBIDao = handle.attach(ReadJDBIDao.class);
      return readJDBIDao.getReadList(userId);
    }
  }

  @Override
  public void insertRead(Read read) {
    try (Handle handle = dbi.open()) {
      ReadJDBIDao readJDBIDao = handle.attach(ReadJDBIDao.class);
      readJDBIDao.insertRead(read.getId(), read.getRelatedId(), read.getReadType(), read.getCreatedByUserId());
    }
  }

  @Override
  public void deleteAllReadDataByUserId(String userId) {
    try (Handle handle = dbi.open()) {
      ReadJDBIDao readJDBIDao = handle.attach(ReadJDBIDao.class);
      readJDBIDao.deleteAllReadDataByUserId(userId);
    }
  }

  @Override
  public void deleteAllReadData() {
    try (Handle handle = dbi.open()) {
      ReadJDBIDao readJDBIDao = handle.attach(ReadJDBIDao.class);
      readJDBIDao.truncateTable();
    }
  }

}
