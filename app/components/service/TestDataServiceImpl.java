package components.service;

import static components.util.RandomUtil.random;
import static components.util.RandomUtil.randomNumber;
import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.ApplicationDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalRequestDao;
import models.Application;
import models.Rfi;
import models.RfiResponse;
import models.StatusUpdate;
import models.enums.ApplicationStatus;
import models.enums.RfiStatus;
import models.enums.StatusType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestDataServiceImpl implements TestDataService {

  private static String APPLICANT = "Kathryn Smith";
  private static String OTHER_APPLICANT = "Christoph";
  private static String OFFICER = "Jerry McGuire";
  private static String GERMANY = "Germany";
  private static String ICELAND = "Iceland";
  private static String FRANCE = "France";

  private static final String APP_ID = randomNumber("ECO");
  private static final String RFI_ID = random("RFI");

  private static final String COMPANY_ID_ONE = "SAR1";
  private static final String COMPANY_ID_TWO = "SAR2";
  private static final String COMPANY_ID_THREE = "SAR3";

  private final RfiDao rfiDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final AmendmentDao amendmentDao;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao, StatusUpdateDao statusUpdateDao, RfiResponseDao rfiResponseDao, ApplicationDao applicationDao, WithdrawalRequestDao withdrawalRequestDao, AmendmentDao amendmentDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentDao = amendmentDao;
  }

  @Override
  public void deleteAllDataAndInsertTestData() {
    applicationDao.deleteAllApplications();
    statusUpdateDao.deleteAllStatusUpdates();
    rfiDao.deleteAllRfiData();
    rfiResponseDao.deleteAllRfiResponses();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    amendmentDao.deleteAllAmendments();

    createApplications();
    applicationDao.insert(createApplication());
    createStatusUpdateTestData().forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData().forEach(rfiDao::insertRfi);
    createRfiResponseTestData().forEach(rfiResponseDao::insertRfiResponse);
  }

  private void createApplications() {
    String companyName = "Company Ltd";
    for (int i = 0; i < 20; i++) {
      String appId = randomNumber("ECO");
      Application app = new Application(appId, COMPANY_ID_ONE, ApplicationStatus.SUBMITTED, APPLICANT, Arrays.asList(GERMANY), getCas(), OFFICER);
      StatusUpdate draft = new StatusUpdate(app.getAppId(), StatusType.DRAFT, time(2017, 3, 3 + i, i, i), null);
      applicationDao.insert(app);
      statusUpdateDao.insertStatusUpdate(draft);
      if (i % 4 != 0) {
        StatusUpdate submitted = new StatusUpdate(app.getAppId(), StatusType.SUBMITTED, time(2017, 4, 3 + i, i, i), null);
        statusUpdateDao.insertStatusUpdate(submitted);
        StatusUpdate initialChecks = new StatusUpdate(app.getAppId(), StatusType.INITIAL_CHECKS, time(2017, 4, 4 + i, i, i), null);
        statusUpdateDao.insertStatusUpdate(initialChecks);
        String rfiId = random("RFI");
        Rfi rfi = new Rfi(rfiId,
            appId,
            RfiStatus.ACTIVE,
            time(2017, 4, 5 + i, i, i),
            time(2017, 5, 5 + i, i, i),
            OFFICER,
            "Please answer this rfi.");
        rfiDao.insertRfi(rfi);
        if (i % 2 != 0) {
          RfiResponse rfiResponse = new RfiResponse(rfiId, APPLICANT, time(2017, 4, 5 + i, i, i), "This is a reply.", null);
          rfiResponseDao.insertRfiResponse(rfiResponse);
        }
      }
    }
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = randomNumber("ECO");
      Application app = new Application(appId, COMPANY_ID_ONE, ApplicationStatus.DRAFT, OTHER_APPLICANT, new ArrayList<>(), getCas(), OFFICER);
      applicationDao.insert(app);
      StatusUpdate draft = new StatusUpdate(app.getAppId(), StatusType.DRAFT, time(2017, 1, 3 + i, i, i), null);
      statusUpdateDao.insertStatusUpdate(draft);
    }
  }

  private String getCas() {
    String cas = "Purchase order: " + randomNumber("GB") + " ";
    if (Math.random() < 0.33) {
      cas = cas + "Thingyma for Deep Blue Holland";
    } else if (Math.random() < 0.66) {
      cas = cas + "Flangable Widget for a Bendy Strut Thing";
    } else {
      cas = cas + "Flangable Widget Corner Piece for a Wobbly Magnetic Thingy";
    }
    return cas;
  }

  private Application createApplication() {
    return new Application(APP_ID, COMPANY_ID_TWO, ApplicationStatus.SUBMITTED, APPLICANT, Arrays.asList(GERMANY, ICELAND, FRANCE), getCas(), OFFICER);
  }

  private List<RfiResponse> createRfiResponseTestData() {
    RfiResponse rfiResponse = new RfiResponse(RFI_ID,
        APPLICANT,
        time(2017, 5, 13, 16, 10),
        "<p>All the items on my application were originally designed for the Eurofighter Typhoon FGR4. "
            + "Please see attached the specifications and design plans showing the original design.</p>"
            + "<p>Kind regards,</p>"
            + "<p>Kathryn Smith</p>",
        null);
    RfiResponse rfiResponseTwo = new RfiResponse(RFI_ID,
        APPLICANT,
        time(2017, 5, 14, 17, 14),
        "This is another test reply.",
        null);
    List<RfiResponse> rfiResponses = new ArrayList<>();
    rfiResponses.add(rfiResponse);
    rfiResponses.add(rfiResponseTwo);
    return rfiResponses;
  }

  private List<Rfi> createRfiTestData() {
    Rfi rfi = new Rfi(random("RFI"),
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 2, 2, 13, 30),
        time(2017, 3, 2, 13, 30),
        OFFICER,
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(RFI_ID,
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        OFFICER,
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(random("RFI"),
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 6, 5, 10, 10),
        time(2018, 6, 5, 10, 10),
        OFFICER,
        "This is another rfi message.");
    List<Rfi> rfiList = new ArrayList<>();
    rfiList.add(rfi);
    rfiList.add(rfiTwo);
    rfiList.add(rfiThree);
    return rfiList;
  }

  private List<StatusUpdate> createStatusUpdateTestData() {
    StatusUpdate draft = new StatusUpdate(APP_ID,
        StatusType.DRAFT,
        time(2017, 1, 1, 0, 0),
        null);
    StatusUpdate submitted = new StatusUpdate(APP_ID,
        StatusType.SUBMITTED,
        time(2017, 2, 1, 14, 12),
        null);
    StatusUpdate initialChecks = new StatusUpdate(APP_ID,
        StatusType.INITIAL_CHECKS,
        time(2017, 2, 2, 13, 30),
        time(2017, 2, 22, 14, 17));
    StatusUpdate technicalAssessment = new StatusUpdate(APP_ID,
        StatusType.TECHNICAL_ASSESSMENT,
        time(2017, 5, 5, 0, 0),
        time(2017, 5, 6, 0, 0));
    StatusUpdate licenseUnitProcessing = new StatusUpdate(APP_ID,
        StatusType.LU_PROCESSING,
        time(2017, 7, 5, 0, 0),
        null);
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    statusUpdates.add(draft);
    statusUpdates.add(submitted);
    statusUpdates.add(initialChecks);
    statusUpdates.add(technicalAssessment);
    statusUpdates.add(licenseUnitProcessing);
    return statusUpdates;
  }

}
