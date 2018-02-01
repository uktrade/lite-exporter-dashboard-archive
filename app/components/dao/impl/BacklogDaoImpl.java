package components.dao.impl;

import com.google.inject.Inject;
import components.dao.BacklogDao;
import components.dao.jdbi.BacklogJDBIDao;
import org.skife.jdbi.v2.DBI;

public class BacklogDaoImpl implements BacklogDao {

  private final BacklogJDBIDao backlogJDBIDao;

  @Inject
  public BacklogDaoImpl(DBI dbi) {
    this.backlogJDBIDao = dbi.onDemand(BacklogJDBIDao.class);
  }

  @Override
  public void insert(long timestamp, String routingKey, String message) {
    backlogJDBIDao.insert(timestamp, routingKey, message);
  }

  @Override
  public void deleteAllBacklogMessages() {
    backlogJDBIDao.truncateTable();
  }

}
