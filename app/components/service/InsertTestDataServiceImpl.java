package components.service;

import com.google.inject.Inject;
import components.dao.StatusUpdateDao;
import models.StatusUpdate;
import models.enums.StatusType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class InsertTestDataServiceImpl implements InsertTestDataService {

  private final StatusUpdateDao statusUpdateDao;

  @Inject
  public InsertTestDataServiceImpl(StatusUpdateDao statusUpdateDao) {
    this.statusUpdateDao = statusUpdateDao;
  }

  @Override
  public void insert() {
    mock().forEach(statusUpdateDao::insertStatusUpdate);
  }

  private List<StatusUpdate> mock() {
    String appId = "ECO123456789012";
    StatusUpdate draft = new StatusUpdate(appId,
        StatusType.DRAFT,
        time(2017, 1, 1, 0, 0),
        null);
    StatusUpdate submitted = new StatusUpdate(appId,
        StatusType.SUBMITTED,
        time(2017, 2, 1, 14, 12),
        null);
    StatusUpdate initialChecks = new StatusUpdate(appId,
        StatusType.INITIAL_CHECKS,
        time(2017, 2, 2, 13, 30),
        time(2017, 2, 22, 14, 17));
    StatusUpdate licenseUnitProcessing = new StatusUpdate(appId,
        StatusType.LU_PROCESSING,
        time(2017, 4, 5, 0, 0),
        null);
    StatusUpdate ogd = new StatusUpdate(appId, StatusType.WITH_OGD, null, null);
    StatusUpdate assessment = new StatusUpdate(appId, StatusType.FINAL_ASSESSMENT, null, null);
    StatusUpdate decision = new StatusUpdate(appId, StatusType.COMPLETE, null, null);
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    statusUpdates.add(draft);
    statusUpdates.add(submitted);
    statusUpdates.add(initialChecks);
    statusUpdates.add(licenseUnitProcessing);
    statusUpdates.add(ogd);
    statusUpdates.add(assessment);
    statusUpdates.add(decision);
    return statusUpdates;
  }

  private long time(int year, int month, int dayOfMonth, int hour, int minute) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }


}
