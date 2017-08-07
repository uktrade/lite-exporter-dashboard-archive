package components.service;

import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.StatusUpdateDao;
import models.Rfi;
import models.RfiResponse;
import models.StatusUpdate;
import models.enums.RfiStatus;
import models.enums.StatusType;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestDataServiceImpl implements TestDataService {

  private static final String APP_ID = "ECO123456789012";
  private static final String RFI_ID = "rfi_123456789";

  private final RfiDao rfiDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiResponseDao rfiResponseDao;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao, StatusUpdateDao statusUpdateDao, RfiResponseDao rfiResponseDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiResponseDao = rfiResponseDao;
  }

  @Override
  public void deleteAllDataAndInsertTestData() {
    statusUpdateDao.deleteAllStatusUpdates();
    rfiDao.deleteAllRfiData();
    rfiResponseDao.deleteAllRfiResponses();
    createStatusUpdateTestData().forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData().forEach(rfiDao::insertRfi);
    createRfiResponseTestData().forEach(rfiResponseDao::insertRfiResponse);
  }

  private List<RfiResponse> createRfiResponseTestData() {
    RfiResponse rfiResponse = new RfiResponse(RFI_ID,
        "Kathryn Smith",
        time(2017, 5, 13, 16, 10),
        "<p>All the items on my application were originally designed for the Eurofighter Typhoon FGR4. "
            + "Please see attached the specifications and design plans showing the original design.</p>"
            + "<p>Kind regards,</p>"
            + "<p>Kathryn Smith</p>",
        null);
    RfiResponse rfiResponseTwo = new RfiResponse(RFI_ID,
        "Kathryn Smith",
        time(2017, 5, 14, 17, 14),
        "This is another test reply.",
        null);
    List<RfiResponse> rfiResponses = new ArrayList<>();
    rfiResponses.add(rfiResponse);
    rfiResponses.add(rfiResponseTwo);
    return rfiResponses;
  }

  private List<Rfi> createRfiTestData() {
    String officer = "Jerry McGuire";
    Rfi rfi = new Rfi(random("rfi"),
        APP_ID,
        RfiStatus.CLOSED,
        time(2017, 2, 2, 13, 30),
        time(2017, 3, 2, 13, 30),
        officer,
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(RFI_ID,
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        officer,
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(random("rfi"),
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 6, 5, 10, 10),
        time(2018, 6, 5, 10, 10),
        officer,
        "This is another rfi message.");
    List<Rfi> rfiList = new ArrayList<>();
    rfiList.add(rfi);
    rfiList.add(rfiTwo);
    rfiList.add(rfiThree);
    return rfiList;
  }

  private List<StatusUpdate> createStatusUpdateTestData() {
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

  private String random(String prefix) {
    return prefix + "_" + UUID.randomUUID().toString().replace("-", "");
  }

  private long time(int year, int month, int dayOfMonth, int hour, int minute) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

}
