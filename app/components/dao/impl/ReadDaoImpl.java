package components.dao.impl;

import components.dao.ReadDao;
import components.dao.jdbi.ReadJDBIDao;
import java.util.List;
import javax.inject.Inject;
import models.Read;
import org.skife.jdbi.v2.DBI;

public class ReadDaoImpl implements ReadDao {

  private final ReadJDBIDao readJDBIDao;

  @Inject
  public ReadDaoImpl(DBI dbi) {
    this.readJDBIDao = dbi.onDemand(ReadJDBIDao.class);
  }

  @Override
  public List<Read> getReadList(String userId) {
    return readJDBIDao.getReadList(userId);
  }

  @Override
  public void insertRead(Read read) {
    readJDBIDao.insertRead(read.getId(), read.getRelatedId(), read.getReadType(), read.getCreatedByUserId());
  }

  @Override
  public void deleteAllReadDataByUserId(String userId) {
    readJDBIDao.deleteAllReadDataByUserId(userId);
  }

  @Override
  public void deleteAllReadData() {
    readJDBIDao.truncateTable();
  }

}
