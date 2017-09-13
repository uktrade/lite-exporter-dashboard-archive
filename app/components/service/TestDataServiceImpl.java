package components.service;

import static components.util.RandomUtil.random;
import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.ApplicationDao;
import components.dao.DraftDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.dao.SielDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalRequestDao;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import models.Application;
import models.Rfi;
import models.RfiResponse;
import models.Siel;
import models.StatusUpdate;
import models.enums.OgelStatus;
import models.enums.RfiStatus;
import models.enums.SielStatus;
import models.enums.StatusType;
import models.view.OgelItemView;
import org.apache.commons.lang3.RandomUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestDataServiceImpl implements TestDataService {

  private static final String APP_QUEUE_ID = "app_queue";

  private static final String APPLICANT_ID = "24492";
  private static final String OTHER_APPLICANT_ID = "2";
  private static final String OFFICER_ID = "3";
  private static final String GERMANY = "Germany";
  private static final String ICELAND = "Iceland";
  private static final String FRANCE = "France";

  private static final String COMPANY_ID_ONE = "SAR1";
  private static final String COMPANY_ID_TWO = "SAR2";
  private static final String COMPANY_ID_THREE = "SAR3";

  private final RfiDao rfiDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiResponseDao rfiResponseDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final AmendmentDao amendmentDao;
  private final DraftDao draftDao;
  private final SielDao sielDao;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao,
                             StatusUpdateDao statusUpdateDao,
                             RfiResponseDao rfiResponseDao,
                             ApplicationDao applicationDao,
                             WithdrawalRequestDao withdrawalRequestDao,
                             AmendmentDao amendmentDao,
                             DraftDao draftDao,
                             SielDao sielDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentDao = amendmentDao;
    this.draftDao = draftDao;
    this.sielDao = sielDao;
  }

  @Override
  public void deleteAllDataAndInsertTwoCompaniesTestData() {
    delete();
    createApplications();
    createSecondUserApplications();
    createAdvancedApplication();
    createSecondUserApplications();
    createSiels();
  }

  @Override
  public void deleteAllDataAndInsertOneCompanyTestData() {
    delete();
    insertCompleteApplication();
    insertNoCaseOfficerApplication();
    createAdvancedApplication();
    createEmptyQueueApplication();
  }

  @Override
  public void deleteAllData() {
    delete();
  }

  @Override
  public void deleteAllDataAndInsertOtherUserApplications() {
    delete();
    createSecondUserApplications();
  }

  private void delete() {
    applicationDao.deleteAllApplications();
    statusUpdateDao.deleteAllStatusUpdates();
    rfiDao.deleteAllRfiData();
    rfiResponseDao.deleteAllRfiResponses();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    amendmentDao.deleteAllAmendments();
    draftDao.deleteAllDrafts();
    sielDao.deleteAllSiels();
  }

  private void createSiels() {
    for (int i = 1; i < 22; i++) {
      String companyId = i % 2 == 0 ? COMPANY_ID_ONE : COMPANY_ID_TWO;
      SielStatus sielStatus = SielStatus.values()[i % SielStatus.values().length];
      List<String> destinationList = i % 2 == 0 ? Arrays.asList(GERMANY) : Arrays.asList(ICELAND, FRANCE);
      Long expiryTimestamp = sielStatus == SielStatus.ACTIVE ? time(2017, 3, i, 15, 10) : time(2016, 3, i, 15, 10);
      Siel siel = new Siel(random("SIE"),
          companyId,
          getApplicantReference(),
          "GBSIE2017/417" + String.format("%02d", i),
          time(2015, 3, i, 15, 10),
          expiryTimestamp,
          sielStatus,
          "SAR1_SITE1",
          destinationList);
      sielDao.insert(siel);
    }
  }

  @Override
  public List<OgelItemView> recycleOgelItemView(OgelItemView base) {
    List<OgelItemView> recycledViews = new ArrayList<>();
    for (int i = 1; i < 22; i++) {
      String add = i % 2 == 0 ? "_A" : "_B";
      long registrationTimestamp = time(2017, 2, 2 + i, 16, 20 + i);
      String registrationDate = TimeUtil.formatDateWithSlashes(registrationTimestamp);
      OgelStatus ogelStatus = OgelStatus.values()[i % (OgelStatus.values().length - 1)];
      String ogelStatusName = LicenceUtil.getOgelStatusName(ogelStatus);
      OgelItemView ogelItemView = new OgelItemView(base.getRegistrationReference(),
          base.getDescription(),
          base.getLicensee() + add,
          base.getSite() + add,
          registrationDate, registrationTimestamp,
          ogelStatusName);
      recycledViews.add(ogelItemView);
    }
    return recycledViews;
  }

  private void createEmptyQueueApplication() {
    Application application = new Application(APP_QUEUE_ID,
        COMPANY_ID_TWO,
        APPLICANT_ID,
        time(2015, 1, 1, 1, 1),
        time(2015, 2, 1, 1, 1),
        Collections.singletonList(GERMANY),
        getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(application);
  }

  private void createApplications() {
    for (int i = 0; i < 20; i++) {
      String appId = random("APP");
      boolean isDraft = i % 4 == 0;
      Long submittedTimestamp = isDraft ? null : time(2017, 4, 3 + i, i, i);
      String caseReference = isDraft ? null : randomNumber("ECO");
      Application app = new Application(appId,
          COMPANY_ID_ONE,
          APPLICANT_ID,
          time(2017, 3, 3 + i, i, i),
          submittedTimestamp,
          Collections.singletonList(GERMANY),
          getApplicantReference(),
          caseReference,
          OFFICER_ID);
      applicationDao.insert(app);
      if (!isDraft) {
        StatusUpdate initialChecks = new StatusUpdate(app.getAppId(),
            StatusType.INITIAL_CHECKS,
            time(2017, 4, 4 + i, i, i),
            null);
        statusUpdateDao.insertStatusUpdate(initialChecks);
        String rfiId = random("RFI");
        Rfi rfi = new Rfi(rfiId,
            appId,
            RfiStatus.ACTIVE,
            time(2017, 4, 5 + i, i, i),
            time(2017, 5, 5 + i, i, i),
            OFFICER_ID,
            "Please answer this rfi.");
        rfiDao.insertRfi(rfi);
        if (i % 2 != 0) {
          RfiResponse rfiResponse = new RfiResponse(rfiId,
              APPLICANT_ID,
              time(2017, 4, 5 + i, i, i),
              "This is a reply.",
              new ArrayList<>());
          rfiResponseDao.insertRfiResponse(rfiResponse);
        }
      }
    }

  }

  private void createSecondUserApplications() {
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = random("APP");
      Application app = new Application(appId,
          COMPANY_ID_ONE,
          OTHER_APPLICANT_ID,
          time(2017, 1, 3 + i, i, i),
          null,
          Collections.singletonList(FRANCE),
          getApplicantReference(),
          null,
          OFFICER_ID);
      applicationDao.insert(app);
    }
  }

  private String getApplicantReference() {
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

  private void createAdvancedApplication() {
    String appId = random("APP");
    String rfiId = random("RFI");
    Application application = new Application(appId,
        COMPANY_ID_TWO,
        APPLICANT_ID,
        time(2016, 11, 4, 13, 10),
        time(2016, 11, 4, 14, 10),
        Arrays.asList(GERMANY, ICELAND, FRANCE),
        getApplicantReference(),
        randomNumber("ECO"), OFFICER_ID);
    applicationDao.insert(application);
    createStatusUpdateTestData(appId).forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData(appId, rfiId).forEach(rfiDao::insertRfi);
    createRfiResponseTestData(rfiId).forEach(rfiResponseDao::insertRfiResponse);
  }

  private List<RfiResponse> createRfiResponseTestData(String rfiId) {
    RfiResponse rfiResponse = new RfiResponse(rfiId,
        APPLICANT_ID,
        time(2017, 5, 13, 16, 10),
        "<p>All the items on my application were originally designed for the Eurofighter Typhoon FGR4. "
            + "Please see attached the specifications and design plans showing the original design.</p>"
            + "<p>Kind regards,</p>"
            + "<p>Kathryn Smith</p>",
        new ArrayList<>());
    RfiResponse rfiResponseTwo = new RfiResponse(rfiId,
        APPLICANT_ID,
        time(2017, 5, 14, 17, 14),
        "This is another test reply.",
        new ArrayList<>());
    List<RfiResponse> rfiResponses = new ArrayList<>();
    rfiResponses.add(rfiResponse);
    rfiResponses.add(rfiResponseTwo);
    return rfiResponses;
  }

  private List<Rfi> createRfiTestData(String appId, String rfiId) {
    Rfi rfi = new Rfi(random("RFI"),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 2, 2, 13, 30),
        time(2017, 3, 2, 13, 30),
        OFFICER_ID,
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(rfiId,
        appId,
        RfiStatus.ACTIVE,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        OFFICER_ID,
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(random("RFI"),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 6, 5, 10, 10),
        time(2018, 6, 5, 10, 10),
        OFFICER_ID,
        "This is another rfi message.");
    List<Rfi> rfiList = new ArrayList<>();
    rfiList.add(rfi);
    rfiList.add(rfiTwo);
    rfiList.add(rfiThree);
    return rfiList;
  }

  private List<StatusUpdate> createStatusUpdateTestData(String appId) {
    StatusUpdate initialChecks = new StatusUpdate(appId,
        StatusType.INITIAL_CHECKS,
        time(2017, 2, 2, 13, 30),
        time(2017, 2, 22, 14, 17));
    StatusUpdate technicalAssessment = new StatusUpdate(appId,
        StatusType.TECHNICAL_ASSESSMENT,
        time(2017, 5, 5, 0, 0),
        time(2017, 5, 6, 0, 0));
    StatusUpdate licenseUnitProcessing = new StatusUpdate(appId,
        StatusType.LU_PROCESSING,
        time(2017, 7, 5, 0, 0),
        null);
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    statusUpdates.add(initialChecks);
    statusUpdates.add(technicalAssessment);
    statusUpdates.add(licenseUnitProcessing);
    return statusUpdates;
  }

  private void insertNoCaseOfficerApplication() {
    String appId = random("APP");
    Application application = new Application(appId,
        COMPANY_ID_TWO,
        APPLICANT_ID,
        time(2016, 11, 3, 3, 3),
        time(2016, 12, 4, 3, 3),
        Collections.singletonList(FRANCE), getApplicantReference(),
        randomNumber("ECO"),
        null);
    applicationDao.insert(application);
    StatusUpdate statusUpdate = new StatusUpdate(appId,
        StatusType.INITIAL_CHECKS,
        time(2016, 12, 5, 3, 3),
        null);
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void insertCompleteApplication() {
    String appId = random("APP");
    Application application = new Application(appId,
        COMPANY_ID_TWO,
        APPLICANT_ID,
        time(2015, 3, 3, 3, 3),
        time(2015, 4, 3, 3, 3),
        Collections.singletonList(FRANCE), getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(application);
    List<StatusType> statusTypes = Arrays.asList(StatusType.INITIAL_CHECKS,
        StatusType.TECHNICAL_ASSESSMENT,
        StatusType.LU_PROCESSING,
        StatusType.WITH_OGD,
        StatusType.FINAL_ASSESSMENT,
        StatusType.COMPLETE);
    for (int i = 0; i < statusTypes.size(); i++) {
      StatusType statusType = statusTypes.get(i);
      Long start = time(2017, 5, 3 + i, 3 + i, 3 + i);
      Long end;
      if (statusType != StatusType.COMPLETE) {
        end = time(2017, 6, 3 + i, 3 + i, 3 + i);
      } else {
        end = null;
      }
      StatusUpdate statusUpdate = new StatusUpdate(appId, statusType, start, end);
      statusUpdateDao.insertStatusUpdate(statusUpdate);
    }
    Rfi rfi = new Rfi(random("RFI"),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 5, 5, 5, 5),
        time(2017, 6, 6, 6, 6),
        OFFICER_ID,
        "This is a rfi.");
    rfiDao.insertRfi(rfi);
  }

  private String randomNumber(String prefix) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      stringBuilder.append(RandomUtils.nextInt(0, 9));
    }
    return prefix + stringBuilder.toString();
  }

}
