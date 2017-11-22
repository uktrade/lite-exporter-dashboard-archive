package components.dao.impl;

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.jdbi.AmendmentRequestJDBIDao;
import components.util.JsonUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import models.AmendmentRequest;
import org.skife.jdbi.v2.DBI;

public class AmendmentRequestDaoImpl implements AmendmentRequestDao {

  private final AmendmentRequestJDBIDao amendmentRequestJDBIDao;

  @Inject
  public AmendmentRequestDaoImpl(DBI dbi) {
    this.amendmentRequestJDBIDao = dbi.onDemand(AmendmentRequestJDBIDao.class);
  }

  @Override
  public List<AmendmentRequest> getAmendmentRequests(String appId) {
    return getAmendmentRequests(Collections.singletonList(appId));
  }

  @Override
  public List<AmendmentRequest> getAmendmentRequests(List<String> appIds) {
    if (appIds.isEmpty()) {
      return new ArrayList<>();
    } else {
      return amendmentRequestJDBIDao.getAmendmentRequests(appIds);
    }
  }

  @Override
  public void insertAmendmentRequest(AmendmentRequest amendmentRequest) {
    amendmentRequestJDBIDao.insertAmendmentRequest(amendmentRequest.getId(),
        amendmentRequest.getAppId(),
        amendmentRequest.getCreatedByUserId(),
        amendmentRequest.getCreatedTimestamp(),
        amendmentRequest.getMessage(),
        JsonUtil.convertListToJson(amendmentRequest.getAttachments()));
  }

  @Override
  public void deleteAllAmendmentRequests() {
    amendmentRequestJDBIDao.truncateTable();
  }

  @Override
  public void deleteAmendmentRequestsByAppId(String appId) {
    amendmentRequestJDBIDao.deleteAmendmentRequestsByAppId(appId);
  }

}
