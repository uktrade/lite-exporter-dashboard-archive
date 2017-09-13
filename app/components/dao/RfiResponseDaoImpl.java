package components.dao;

import com.google.inject.Inject;
import components.util.JsonUtil;
import models.RfiResponse;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.util.ArrayList;
import java.util.List;

public class RfiResponseDaoImpl implements RfiResponseDao {

  private final DBI dbi;

  @Inject
  public RfiResponseDaoImpl(DBI dbi) {
    this.dbi = dbi;
  }

  @Override
  public List<RfiResponse> getRfiResponses(List<String> rfiIds) {
    if (rfiIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      try (final Handle handle = dbi.open()) {
        RfiResponseJDBIDao rfiResponseJDBIDao = handle.attach(RfiResponseJDBIDao.class);
        return rfiResponseJDBIDao.getRfiResponses(rfiIds);
      }
    }
  }

  @Override
  public RfiResponse getRfiResponse(String rfiId) {
    try (final Handle handle = dbi.open()) {
      RfiResponseJDBIDao rfiResponseJDBIDao = handle.attach(RfiResponseJDBIDao.class);
      return rfiResponseJDBIDao.getRfiResponse(rfiId);
    }
  }

  @Override
  public void insertRfiResponse(RfiResponse rfiResponse) {
    try (final Handle handle = dbi.open()) {
      RfiResponseJDBIDao rfiResponseJDBIDao = handle.attach(RfiResponseJDBIDao.class);
      rfiResponseJDBIDao.insertRfiResponse(rfiResponse.getRfiId(),
          rfiResponse.getSentBy(),
          rfiResponse.getSentTimestamp(),
          rfiResponse.getMessage(),
          JsonUtil.convertFilesToJson(rfiResponse.getAttachments()));
    }
  }

  @Override
  public void deleteAllRfiResponses() {
    try (final Handle handle = dbi.open()) {
      RfiResponseJDBIDao rfiResponseJDBIDao = handle.attach(RfiResponseJDBIDao.class);
      rfiResponseJDBIDao.truncateTable();
    }
  }

}
