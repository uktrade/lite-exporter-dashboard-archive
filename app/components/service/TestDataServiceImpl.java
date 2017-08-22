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
import models.enums.RfiStatus;
import models.enums.StatusType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestDataServiceImpl implements TestDataService {

  private static final String APPLICANT_ID = "24492";
  private static final String OTHER_APPLICANT_ID = "2";
  private static final String OFFICER_ID = "3";
  private static final String GERMANY = "Germany";
  private static final String ICELAND = "Iceland";
  private static final String FRANCE = "France";

  private static final String APP_ID = random("APP");
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
  public TestDataServiceImpl(RfiDao rfiDao,
                             StatusUpdateDao statusUpdateDao,
                             RfiResponseDao rfiResponseDao,
                             ApplicationDao applicationDao,
                             WithdrawalRequestDao withdrawalRequestDao,
                             AmendmentDao amendmentDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiResponseDao = rfiResponseDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentDao = amendmentDao;
  }

  @Override
  public void deleteAllDataAndInsertTwoCompaniesTestData() {
    delete();
    createApplications();
    createAdvancedApplication();
  }

  @Override
  public void deleteAllDataAndInsertOneCompanyTestData() {
    delete();
    insertCompleteApplication();
    insertNoCaseOfficerApplication();
    createAdvancedApplication();
  }

  @Override
  public void deleteAllData() {
    delete();
  }

  private void delete() {
    applicationDao.deleteAllApplications();
    statusUpdateDao.deleteAllStatusUpdates();
    rfiDao.deleteAllRfiData();
    rfiResponseDao.deleteAllRfiResponses();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    amendmentDao.deleteAllAmendments();
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
          getCas(),
          caseReference,
          OFFICER_ID);
      applicationDao.insert(app);
      if (!isDraft) {
        StatusUpdate initialChecks = new StatusUpdate(app.getAppId(), StatusType.INITIAL_CHECKS, time(2017, 4, 4 + i, i, i), null);
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
          RfiResponse rfiResponse = new RfiResponse(rfiId, APPLICANT_ID, time(2017, 4, 5 + i, i, i), "This is a reply.", null);
          rfiResponseDao.insertRfiResponse(rfiResponse);
        }
      }
    }
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = random("APP");
      Application app = new Application(appId,
          COMPANY_ID_ONE,
          OTHER_APPLICANT_ID,
          time(2017, 1, 3 + i, i, i),
          null,
          Collections.singletonList(FRANCE),
          getCas(),
          null,
          OFFICER_ID);
      applicationDao.insert(app);
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

  private void createAdvancedApplication() {
    Application application = new Application(APP_ID, COMPANY_ID_TWO, APPLICANT_ID, time(2016, 11, 4, 13, 10), time(2016, 11, 4, 14, 10), Arrays.asList(GERMANY, ICELAND, FRANCE), getCas(), randomNumber("ECO"), OFFICER_ID);
    applicationDao.insert(application);
    createStatusUpdateTestData().forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData().forEach(rfiDao::insertRfi);
    createRfiResponseTestData().forEach(rfiResponseDao::insertRfiResponse);
  }

  private List<RfiResponse> createRfiResponseTestData() {
    RfiResponse rfiResponse = new RfiResponse(RFI_ID,
        APPLICANT_ID,
        time(2017, 5, 13, 16, 10),
        "<p>All the items on my application were originally designed for the Eurofighter Typhoon FGR4. "
            + "Please see attached the specifications and design plans showing the original design.</p>"
            + "<p>Kind regards,</p>"
            + "<p>Kathryn Smith</p>",
        null);
    RfiResponse rfiResponseTwo = new RfiResponse(RFI_ID,
        APPLICANT_ID,
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
        OFFICER_ID,
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(RFI_ID,
        APP_ID,
        RfiStatus.ACTIVE,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        OFFICER_ID,
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(random("RFI"),
        APP_ID,
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

  private List<StatusUpdate> createStatusUpdateTestData() {
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
        Collections.singletonList(FRANCE), getCas(),
        randomNumber("ECO"),
        null);
    applicationDao.insert(application);
    StatusUpdate statusUpdate = new StatusUpdate(appId, StatusType.INITIAL_CHECKS, time(2016, 12, 5, 3, 3), null);
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void insertCompleteApplication() {
    String appId = random("APP");
    Application application = new Application(appId,
        COMPANY_ID_TWO,
        APPLICANT_ID,
        time(2015, 3, 3, 3, 3),
        time(2015, 4, 3, 3, 3),
        Collections.singletonList(FRANCE), getCas(),
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
  }

}