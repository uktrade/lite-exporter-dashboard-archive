package components.service.test;

import static components.util.RandomIdUtil.amendmentId;
import static components.util.RandomIdUtil.appId;
import static components.util.RandomIdUtil.delayNotificationId;
import static components.util.RandomIdUtil.documentId;
import static components.util.RandomIdUtil.fileId;
import static components.util.RandomIdUtil.informNotificationId;
import static components.util.RandomIdUtil.outcomeId;
import static components.util.RandomIdUtil.rfiId;
import static components.util.RandomIdUtil.rfiReplyId;
import static components.util.RandomIdUtil.rfiWithdrawalId;
import static components.util.RandomIdUtil.statusUpdateId;
import static components.util.RandomIdUtil.stopNotificationId;
import static components.util.RandomIdUtil.withdrawalApprovalId;
import static components.util.RandomIdUtil.withdrawalRejectionId;
import static components.util.RandomIdUtil.withdrawalRequestId;
import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.ApplicationDao;
import components.dao.CaseDetailsDao;
import components.dao.DraftDao;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.ReadDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import components.service.UserPermissionService;
import components.util.RandomIdUtil;
import components.util.TestUtil;
import components.util.TimeUtil;
import models.AmendmentRequest;
import models.Application;
import models.CaseDetails;
import models.Document;
import models.File;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.WithdrawalRequest;
import models.enums.DocumentType;
import models.enums.DraftType;
import models.enums.StatusType;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.LicenceView.Type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TestDataServiceImpl implements TestDataService {

  public static final String ADMIN = "1";
  public static final String APPLICANT_ID = "24492";
  private static final String APPLICANT_TWO_ID = "24493";
  public static final String APPLICANT_THREE_ID = "24494";
  private static final List<String> RECIPIENTS = Arrays.asList(APPLICANT_ID, APPLICANT_TWO_ID, APPLICANT_THREE_ID);

  public static final String OTHER_APPLICANT_ID = "2";
  public static final String OFFICER_ID = "3";
  public static final String OTHER_OFFICER_ID = "300";

  private static final String APP_QUEUE_ID = "app_queue";

  private static final String SOUTH_GEORGIA = "South Georgia and South Sandwich Islands";
  private static final String FEDERATION = "St Christopher and Nevis, Federation of";
  private static final String GERMANY = "Germany";
  private static final String ICELAND = "Iceland";
  private static final String FRANCE = "France";

  private static final List<String> CONSIGNEE_COUNTRIES = Collections.singletonList(FRANCE);
  private static final List<String> END_USER_COUNTRIES = new ArrayList<>();
  private static final List<String> CONSIGNEE_COUNTRIES_TWO = Collections.singletonList(GERMANY);
  private static final List<String> END_USER_COUNTRIES_TWO = Arrays.asList(GERMANY, FRANCE, ICELAND);
  private static final List<String> CONSIGNEE_COUNTRIES_THREE = Collections.singletonList(SOUTH_GEORGIA);
  private static final List<String> END_USER_COUNTRIES_THREE = Collections.singletonList(FEDERATION);
  private static final List<String> CONSIGNEE_COUNTRIES_FOUR = Collections.singletonList(ICELAND);
  private static final List<String> END_USER_COUNTRIES_FOUR = Collections.singletonList(FRANCE);
  private static final List<String> CONSIGNEE_COUNTRIES_FIVE = Collections.singletonList(FRANCE);
  private static final List<String> END_USER_COUNTRIES_FIVE = Collections.singletonList(FRANCE);

  public static final String COMPANY_ID_ONE = "SAR1";
  private static final String COMPANY_ID_TWO = "SAR2";
  private static final String COMPANY_ID_THREE = "SAR3";
  public static final List<String> COMPANY_IDS = Arrays.asList(TestDataServiceImpl.COMPANY_ID_ONE,
      TestDataServiceImpl.COMPANY_ID_TWO,
      TestDataServiceImpl.COMPANY_ID_THREE);
  public static final String SITE_ID = "SITE1";

  private static final List<DocumentType> ISSUE_DOCUMENT_TYPES = Arrays.asList(DocumentType.ISSUE_COVER_LETTER,
      DocumentType.ISSUE_LICENCE_DOCUMENT,
      DocumentType.ISSUE_REFUSE_DOCUMENT,
      DocumentType.ISSUE_NLR_DOCUMENT,
      DocumentType.ISSUE_AMENDMENT_LETTER);

  private static final List<DocumentType> AMEND_DOCUMENT_TYPES = Arrays.asList(DocumentType.AMENDMENT_COVER_LETTER,
      DocumentType.AMENDMENT_LICENCE_DOCUMENT,
      DocumentType.AMENDMENT_REFUSE_DOCUMENT,
      DocumentType.AMENDMENT_NLR_DOCUMENT,
      DocumentType.AMENDMENT_AMENDMENT_LETTER);

  private static final List<String> LICENCE_REFERENCES;

  static {
    String letter = "Cover letter";
    String licence = "Licence SIE2017/000001 granted for some or all of your items";
    String refusal = "Letter explaining the licence refusal of some or all of your items";
    String nlr = "Letter confirming that no licence is required for some or all of your items";
    LICENCE_REFERENCES = Arrays.asList(letter, licence, refusal, nlr);
  }

  private final RfiDao rfiDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiReplyDao rfiReplyDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final AmendmentRequestDao amendmentRequestDao;
  private final DraftDao draftDao;
  private final OutcomeDao outcomeDao;
  private final NotificationDao notificationDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final ReadDao readDao;
  private final UserPermissionService userPermissionService;
  private final CaseDetailsDao caseDetailsDao;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao,
                             StatusUpdateDao statusUpdateDao,
                             RfiReplyDao rfiReplyDao,
                             ApplicationDao applicationDao,
                             WithdrawalRequestDao withdrawalRequestDao,
                             AmendmentRequestDao amendmentRequestDao,
                             DraftDao draftDao,
                             OutcomeDao outcomeDao,
                             NotificationDao notificationDao,
                             WithdrawalRejectionDao withdrawalRejectionDao,
                             WithdrawalApprovalDao withdrawalApprovalDao,
                             RfiWithdrawalDao rfiWithdrawalDao,
                             ReadDao readDao,
                             UserPermissionService userPermissionService,
                             CaseDetailsDao caseDetailsDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiReplyDao = rfiReplyDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentRequestDao = amendmentRequestDao;
    this.draftDao = draftDao;
    this.outcomeDao = outcomeDao;
    this.notificationDao = notificationDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.readDao = readDao;
    this.userPermissionService = userPermissionService;
    this.caseDetailsDao = caseDetailsDao;
  }

  @Override
  public void deleteAllUsersAndInsertStartData() {
    deleteAllUsers();
    insertOneCompany(TestDataServiceImpl.APPLICANT_ID);
    insertTwoCompanies(TestDataServiceImpl.APPLICANT_TWO_ID);
    insertOtherUserApplications(TestDataServiceImpl.APPLICANT_THREE_ID);
  }

  @Override
  public void insertOneCompany(String userId) {
    createCompletedApplications(userId);
    createNoCaseOfficerApplication(userId);
    createAdvancedApplication(userId);
    createEmptyQueueApplication(userId);
    createWithdrawnOrStoppedApplication(userId, false);
    createWithdrawnOrStoppedApplication(userId, true);
  }

  @Override
  public void insertTwoCompanies(String userId) {
    createDraftApplications(userId);
    createApplications(userId);
    createSecondUserApplications(userId);
    createAdvancedApplication(userId);
  }

  @Override
  public void insertOtherUserApplications(String userId) {
    createSecondUserApplications(userId);
  }

  @Override
  public void deleteCurrentUser(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    List<Application> applications = applicationDao.getApplicationsByCustomerIds(customerIds);

    List<String> appIds = applications.stream()
        .map(Application::getId)
        .distinct()
        .collect(Collectors.toList());

    List<String> caseReferences = caseDetailsDao.getCaseDetailsListByAppIds(appIds).stream()
        .map(CaseDetails::getCaseReference)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    caseReferences.forEach(outcomeDao::deleteOutcome);
    appIds.forEach(statusUpdateDao::deleteStatusUpdatesByAppId);
    appIds.forEach(withdrawalRequestDao::deleteWithdrawalRequestsByAppId);
    appIds.forEach(withdrawalRejectionDao::deleteWithdrawalRejectionsByAppId);
    appIds.forEach(withdrawalApprovalDao::deleteWithdrawalApprovalsByAppId);
    appIds.forEach(amendmentRequestDao::deleteAmendmentRequestsByAppId);
    List<String> rfiIds = rfiDao.getRfiList(caseReferences).stream()
        .map(Rfi::getId)
        .collect(Collectors.toList());
    rfiIds.forEach(rfiReplyDao::deleteRfiRepliesByRfiId);
    rfiIds.forEach(rfiWithdrawalDao::deleteRfiWithdrawalByRfiId);
    rfiIds.forEach(rfiId -> draftDao.deleteDraft(rfiId, DraftType.RFI_REPLY));
    appIds.forEach(appId -> draftDao.deleteDraft(appId, DraftType.AMENDMENT_OR_WITHDRAWAL));
    caseReferences.forEach(notificationDao::deleteNotifications);
    readDao.deleteAllReadDataByUserId(userId);
    caseReferences.forEach(rfiDao::deleteRfiByCaseReference);
    caseReferences.forEach(caseDetailsDao::deleteCaseDetails);
    appIds.forEach(applicationDao::deleteApplication);
  }

  @Override
  public void deleteAllUsers() {
    statusUpdateDao.deleteAllStatusUpdates();
    rfiReplyDao.deleteAllRfiReplies();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    withdrawalRejectionDao.deleteAllWithdrawalRejections();
    withdrawalApprovalDao.deleteAllWithdrawalApprovals();
    amendmentRequestDao.deleteAllAmendmentRequests();
    draftDao.deleteAllDrafts();
    outcomeDao.deleteAllOutcomes();
    notificationDao.deleteAllNotifications();
    rfiWithdrawalDao.deleteAllRfiWithdrawals();
    readDao.deleteAllReadData();
    rfiDao.deleteAllRfiData();
    caseDetailsDao.deleteAllCaseDetails();
    applicationDao.deleteAllApplications();
  }

  private void createEmptyQueueApplication(String userId) {
    Application application = new Application(userId + "_" + APP_QUEUE_ID,
        TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2015, 1, 1, 1, 1),
        time(2015, 2, 1, 1, 1),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        OFFICER_ID,
        SITE_ID);
    CaseDetails caseDetails = new CaseDetails(application.getId(),
        RandomIdUtil.caseReference(),
        OFFICER_ID,
        application.getCreatedTimestamp());
    caseDetailsDao.insert(caseDetails);
    applicationDao.update(application);
  }

  private void createDraftApplications(String userId) {
    for (int i = 0; i < 4; i++) {
      String appId = appId();
      Application app = new Application(appId,
          TestUtil.wrapCustomerId(userId, COMPANY_ID_ONE),
          userId,
          time(2017, 3, 1 + i, 0, 0),
          null,
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          SITE_ID);
      applicationDao.update(app);
    }
  }

  private void createApplications(String userId) {
    for (int i = 0; i < 20; i++) {
      String appId = appId();
      Long submittedTimestamp = time(2017, 4, 6 + i, i, i);
      String caseReference = RandomIdUtil.caseReference();
      Application app = new Application(appId,
          TestUtil.wrapCustomerId(userId, COMPANY_ID_ONE),
          userId,
          time(2017, 3, 3 + i, i, i),
          submittedTimestamp,
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          SITE_ID);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          app.getCreatedTimestamp());
      caseDetailsDao.insert(caseDetails);
      applicationDao.update(app);
      StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
          app.getId(),
          StatusType.INITIAL_CHECKS,
          time(2017, 4, 4 + i, i, i));
      statusUpdateDao.insertStatusUpdate(initialChecks);
      String rfiId = rfiId();
      Rfi rfi = new Rfi(rfiId,
          caseReference,
          time(2017, 4, 5 + i, i, i),
          time(2017, 5, 5 + i, i, i),
          OFFICER_ID,
          RECIPIENTS,
          "Please answer this rfi.");
      rfiDao.insertRfi(rfi);
      String rfiTwoId = rfiId();
      Rfi rfiTwo = new Rfi(rfiTwoId,
          caseReference,
          time(2017, 6, 5 + i, i, i),
          time(2017, 7, 5 + i, i, i),
          OFFICER_ID,
          RECIPIENTS,
          "Please also answer this rfi.");
      rfiDao.insertRfi(rfiTwo);
      if (i % 3 != 0) {
        RfiReply rfiReply = new RfiReply();
        rfiReply.setId(rfiReplyId());
        rfiReply.setRfiId(rfiId);
        rfiReply.setCreatedByUserId(userId);
        rfiReply.setCreatedTimestamp(time(2017, 4, 5 + i, i, i));
        rfiReply.setMessage("This is a reply.");
        rfiReply.setAttachments(new ArrayList<>());
        rfiReplyDao.insertRfiReply(rfiReply);
        RfiWithdrawal rfiWithdrawal = new RfiWithdrawal(rfiWithdrawalId(),
            rfiTwoId,
            OFFICER_ID,
            time(2017, 8, 5 + i, i, i),
            RECIPIENTS,
            "This rfi has been withdrawn.");
        rfiWithdrawalDao.insertRfiWithdrawal(rfiWithdrawal);

      }
      if (i % 3 == 0) {
        File document = new File(fileId(), "Inform letter", "#");
        Notification notification = new Notification(informNotificationId(),
            caseReference,
            NotificationType.INFORM,
            OFFICER_ID,
            time(2017, 5, 1 + i, 2, 3),
            RECIPIENTS,
            null,
            document);
        notificationDao.insertNotification(notification);
      }
    }
  }

  private void createSecondUserApplications(String userId) {
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = appId();
      Application app = new Application(appId,
          TestUtil.wrapCustomerId(userId, COMPANY_ID_ONE),
          OTHER_APPLICANT_ID,
          time(2017, 1, 3 + i, i, i),
          null,
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          SITE_ID);
      CaseDetails caseDetails = new CaseDetails(appId,
          RandomIdUtil.caseReference(),
          OFFICER_ID,
          app.getCreatedTimestamp());
      caseDetailsDao.insert(caseDetails);
      applicationDao.update(app);
    }
    // Create application with inform notice
    String appId = appId();
    String caseReference = RandomIdUtil.caseReference();
    Application application = new Application(appId,
        TestUtil.wrapCustomerId(userId, COMPANY_ID_ONE),
        OTHER_APPLICANT_ID,
        time(2017, 1, 7, 1, 1),
        time(2017, 1, 8, 1, 1),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        OFFICER_ID,
        SITE_ID);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        application.getCreatedTimestamp());
    caseDetailsDao.insert(caseDetails);
    applicationDao.update(application);
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        application.getId(),
        StatusType.INITIAL_CHECKS,
        time(2017, 8, 3, 0, 0));
    statusUpdateDao.insertStatusUpdate(initialChecks);
    File document = new File(fileId(), "Inform letter", "#");
    Notification notification = new Notification(informNotificationId(),
        caseReference,
        NotificationType.INFORM,
        OFFICER_ID,
        time(2017, 9, 1, 2, 3),
        RECIPIENTS,
        null,
        document);
    notificationDao.insertNotification(notification);
  }

  private String getApplicantReference() {
    String cas = "Purchase order: " + RandomIdUtil.randomNumber("GB") + " ";
    if (Math.random() < 0.33) {
      cas = cas + "Thingyma for Deep Blue Holland";
    } else if (Math.random() < 0.66) {
      cas = cas + "Flangable Widget for a Bendy Strut Thing";
    } else {
      cas = cas + "Flangable Widget Corner Piece for a Wobbly Magnetic Thingy";
    }
    return cas;
  }

  private void createWithdrawnOrStoppedApplication(String userId, boolean stopped) {
    String appId = appId();
    String caseReference = RandomIdUtil.caseReference();
    List<String> consigneeCountries;
    List<String> endUserCountries;
    if (stopped) {
      consigneeCountries = CONSIGNEE_COUNTRIES_TWO;
      endUserCountries = END_USER_COUNTRIES_TWO;
    } else {
      consigneeCountries = CONSIGNEE_COUNTRIES_THREE;
      endUserCountries = END_USER_COUNTRIES_THREE;
    }
    Application application = new Application(appId,
        TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2013, 11, 4, 13, 10),
        time(2013, 11, 4, 14, 10),
        consigneeCountries,
        endUserCountries,
        getApplicantReference(),
        OFFICER_ID,
        SITE_ID);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        application.getCreatedTimestamp());
    caseDetailsDao.insert(caseDetails);
    applicationDao.update(application);
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        time(2013, 12, 5, 3, 3));
    statusUpdateDao.insertStatusUpdate(initialChecks);
    StatusUpdate technicalAssessment = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.TECHNICAL_ASSESSMENT,
        time(2015, 5, 6, 13, 10));
    statusUpdateDao.insertStatusUpdate(technicalAssessment);

    AmendmentRequest amendmentRequest = new AmendmentRequest();
    amendmentRequest.setId(amendmentId());
    amendmentRequest.setAppId(appId);
    amendmentRequest.setCreatedByUserId(userId);
    amendmentRequest.setCreatedTimestamp(time(2014, 11, 5, 14, 17));
    amendmentRequest.setAttachments(new ArrayList<>());
    amendmentRequest.setMessage("This is an amendment.");
    amendmentRequestDao.insertAmendmentRequest(amendmentRequest);

    for (int i = 0; i < 4; i++) {
      WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
      withdrawalRequest.setId(withdrawalRequestId());
      withdrawalRequest.setAppId(appId);
      withdrawalRequest.setCreatedByUserId(userId);
      withdrawalRequest.setCreatedTimestamp(time(2015, 1 + 2 * i, 5, 13, 10));
      withdrawalRequest.setMessage("This is a withdrawal request.");
      withdrawalRequest.setAttachments(new ArrayList<>());
      withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
      if (i != 3) {
        Long createdTimestamp = time(2015, 1 + 2 * i + 1, 5, 13, 10);
        WithdrawalRejection withdrawalRejection = new WithdrawalRejection(withdrawalRejectionId(),
            appId,
            userId,
            createdTimestamp,
            RECIPIENTS,
            "");
        withdrawalRejectionDao.insertWithdrawalRejection(withdrawalRejection);
      }
    }

    Notification delayNotification = new Notification(
        delayNotificationId(),
        caseReference,
        NotificationType.DELAY,
        null,
        time(2016, 1, 1, 13, 20),
        RECIPIENTS,
        "We're sorry to inform you that your application has been delayed.",
        null);
    notificationDao.insertNotification(delayNotification);

    if (stopped) {
      Notification stopNotification = new Notification(
          stopNotificationId(),
          caseReference,
          NotificationType.STOP,
          TestDataServiceImpl.OFFICER_ID,
          time(2017, 1, 1, 14, 30),
          RECIPIENTS,
          "We have had to stop your application.",
          null);
      notificationDao.insertNotification(stopNotification);
    } else {
      WithdrawalApproval withdrawalApproval = new WithdrawalApproval(withdrawalApprovalId(),
          appId,
          OFFICER_ID,
          time(2017, 1, 5, 13, 10),
          RECIPIENTS,
          null);
      withdrawalApprovalDao.insertWithdrawalApproval(withdrawalApproval);
    }
  }

  private void createAdvancedApplication(String userId) {
    String appId = appId();
    String caseReference = RandomIdUtil.caseReference();
    String rfiId = rfiId();
    Application application = new Application(appId,
        TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2016, 11, 4, 13, 10),
        time(2016, 11, 4, 14, 10),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        OFFICER_ID,
        SITE_ID);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        application.getCreatedTimestamp());
    caseDetailsDao.insert(caseDetails);
    applicationDao.update(application);
    createStatusUpdateTestData(appId).forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData(caseReference, rfiId).forEach(rfiDao::insertRfi);
    rfiReplyDao.insertRfiReply(createRfiReplyTestData(userId, rfiId));
  }

  private RfiReply createRfiReplyTestData(String userId, String rfiId) {
    RfiReply rfiReply = new RfiReply();
    rfiReply.setId(rfiReplyId());
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedByUserId(userId);
    rfiReply.setCreatedTimestamp(time(2017, 5, 13, 16, 10));
    rfiReply.setMessage("<p>All the items on my application were originally designed for the Eurofighter Typhoon FGR4. "
        + "Please see attached the specifications and design plans showing the original design.</p>"
        + "<p>Kind regards,</p>"
        + "<p>Kathryn Smith</p>");
    rfiReply.setAttachments(new ArrayList<>());
    return rfiReply;
  }

  private List<Rfi> createRfiTestData(String caseReference, String rfiId) {
    Rfi rfi = new Rfi(rfiId(),
        caseReference,
        time(2017, 1, 2, 13, 30),
        time(2017, 2, 2, 13, 30),
        OFFICER_ID,
        new ArrayList<>(),
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(rfiId,
        caseReference,
        time(2017, 2, 5, 10, 10),
        time(2017, 3, 12, 16, 10),
        OFFICER_ID,
        new ArrayList<>(),
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(rfiId(),
        caseReference,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        OFFICER_ID,
        new ArrayList<>(),
        "This is some rfi message.");
    Rfi rfiFour = new Rfi(rfiId(),
        caseReference,
        time(2017, 7, 5, 10, 10),
        time(2018, 8, 5, 10, 10),
        OFFICER_ID,
        new ArrayList<>(),
        "This is another rfi message.");
    List<Rfi> rfiList = new ArrayList<>();
    rfiList.add(rfi);
    rfiList.add(rfiTwo);
    rfiList.add(rfiThree);
    rfiList.add(rfiFour);
    return rfiList;
  }

  private List<StatusUpdate> createStatusUpdateTestData(String appId) {
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        time(2017, 1, 2, 13, 30));
    StatusUpdate technicalAssessment = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.TECHNICAL_ASSESSMENT,
        time(2017, 5, 5, 0, 0));
    StatusUpdate licenseUnitProcessing = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.LU_PROCESSING,
        time(2017, 7, 5, 0, 0));
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    statusUpdates.add(initialChecks);
    statusUpdates.add(technicalAssessment);
    statusUpdates.add(licenseUnitProcessing);
    return statusUpdates;
  }

  private void createNoCaseOfficerApplication(String userId) {
    String appId = appId();
    Application application = new Application(appId,
        TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2016, 11, 3, 3, 3),
        time(2016, 12, 4, 3, 3),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        null,
        SITE_ID);
    CaseDetails caseDetails = new CaseDetails(appId,
        RandomIdUtil.caseReference(),
        OFFICER_ID,
        application.getCreatedTimestamp());
    caseDetailsDao.insert(caseDetails);
    applicationDao.update(application);
    StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        time(2016, 12, 5, 3, 3));
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void createCompletedApplications(String userId) {
    for (int k = 0; k < 10; k++) {
      String appId = appId();
      String caseReference = RandomIdUtil.caseReference();
      List<String> consigneeCountries;
      List<String> endUserCountries;
      if (k == 0) {
        consigneeCountries = CONSIGNEE_COUNTRIES_FOUR;
        endUserCountries = END_USER_COUNTRIES_FOUR;
      } else {
        consigneeCountries = CONSIGNEE_COUNTRIES_FIVE;
        endUserCountries = END_USER_COUNTRIES_FIVE;
      }
      Application application = new Application(appId,
          TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO),
          userId,
          time(2015, 3, 3, 3 + k, 3),
          time(2015, 4, 3, 3, 3),
          consigneeCountries,
          endUserCountries,
          getApplicantReference(),
          OFFICER_ID,
          SITE_ID);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          application.getCreatedTimestamp());
      caseDetailsDao.insert(caseDetails);
      applicationDao.update(application);
      List<StatusType> statusTypes = Arrays.asList(StatusType.INITIAL_CHECKS,
          StatusType.TECHNICAL_ASSESSMENT,
          StatusType.LU_PROCESSING,
          StatusType.WITH_OGD,
          StatusType.FINAL_ASSESSMENT,
          StatusType.COMPLETE);
      for (int i = 0; i < statusTypes.size(); i++) {
        StatusType statusType = statusTypes.get(i);
        Long createdTimestamp = time(2016, 5, 3 + i, 3 + i, 3 + i);
        StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(), appId, statusType, createdTimestamp);
        statusUpdateDao.insertStatusUpdate(statusUpdate);
      }

      long outcomeCreatedTimestamp = time(2016, 7, 10, 13, 17);
      List<Document> issueDocuments = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = ISSUE_DOCUMENT_TYPES.get(j);
        Document document = new Document(documentId(),
            documentType,
            LICENCE_REFERENCES.get(j),
            UUID.randomUUID().toString() + ".pdf",
            "#");
        issueDocuments.add(document);
      }
      Outcome outcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, RECIPIENTS, outcomeCreatedTimestamp, issueDocuments);
      outcomeDao.insertOutcome(outcome);

      if (k > 4) {
        insertCompletedCase(appId);
      }
      if (k == 1 || k == 6) {
        insertCase(appId, false, false, false, false);
      } else if (k == 2 || k == 7) {
        insertCase(appId, false, true, true, false);
      } else if (k == 3 || k == 8) {
        insertCase(appId, true, false, true, false);
      } else if (k == 4 || k == 9) {
        insertCase(appId, false, false, true, true);
      }

      for (int i = 0; i < 3; i++) {
        long informCreatedTimestamp = time(2016, 8 + i, 3 + i, 3 + i, 3 + i);
        File document = new File(fileId(), "Licence required inform letter number " + (i + 1), "#");
        Notification notification = new Notification(informNotificationId(), caseReference, NotificationType.INFORM, OFFICER_ID, informCreatedTimestamp, RECIPIENTS, null, document);
        notificationDao.insertNotification(notification);
      }
    }
  }

  private void insertCompletedCase(String appId) {
    long createdTimestamp = time(2016, 12, 20, 20, 20);
    String caseReference = RandomIdUtil.caseReference();
    CaseDetails caseDetails = new CaseDetails(appId, caseReference, OFFICER_ID, createdTimestamp);
    caseDetailsDao.insert(caseDetails);
    List<Document> documents = new ArrayList<>();
    for (int j = 0; j < 4; j++) {
      DocumentType documentType = AMEND_DOCUMENT_TYPES.get(j);
      Document document = new Document(documentId(),
          documentType,
          LICENCE_REFERENCES.get(j),
          UUID.randomUUID().toString() + ".pdf",
          "#");
      documents.add(document);
    }
    long outcomeCreatedTimestamp = time(2016, 12, 22, 13, 17);
    Outcome amendOutcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, RECIPIENTS, outcomeCreatedTimestamp, documents);
    outcomeDao.insertOutcome(amendOutcome);
  }

  private void insertCase(String appId, boolean hasOutcome, boolean hasRfi, boolean hasInformLetter, boolean isStopped) {
    long createdTimestamp = time(2017, 2, 2, 2, 2);
    String caseReference = RandomIdUtil.caseReference();
    CaseDetails caseDetails = new CaseDetails(appId, caseReference, OFFICER_ID, createdTimestamp);
    caseDetailsDao.insert(caseDetails);
    if (hasRfi) {
      for (int i = 0; i < 2; i++) {
        Rfi rfi = new Rfi(RandomIdUtil.rfiId(),
            caseReference,
            time(2017, 3 + i, 2, 2, 2),
            time(2018, 3 + i, 2, 2, 2),
            OFFICER_ID,
            RECIPIENTS,
            "Please answer this rfi");
        rfiDao.insertRfi(rfi);
      }
    }
    if (hasInformLetter) {
      long informCreatedTimestamp = time(2017, 4, 4, 4, 4);
      File document = new File(fileId(), "Licence required inform letter number 4", "#");
      Notification notification = new Notification(informNotificationId(), caseReference, NotificationType.INFORM, OFFICER_ID, informCreatedTimestamp, RECIPIENTS, null, document);
      notificationDao.insertNotification(notification);
    }
    if (isStopped) {
      Notification stopNotification = new Notification(stopNotificationId(),
          caseReference,
          NotificationType.STOP,
          TestDataServiceImpl.OFFICER_ID,
          time(2017, 5, 1, 14, 30),
          RECIPIENTS,
          "We have had to stop your application.",
          null);
      notificationDao.insertNotification(stopNotification);
    }
    if (hasOutcome) {
      List<Document> documents = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = AMEND_DOCUMENT_TYPES.get(j);
        Document document = new Document(documentId(),
            documentType,
            LICENCE_REFERENCES.get(j),
            UUID.randomUUID().toString() + ".pdf",
            "#");
        documents.add(document);
      }
      long outcomeCreatedTimestamp = time(2017, 3, 10, 13, 17);
      Outcome amendOutcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, RECIPIENTS, outcomeCreatedTimestamp, documents);
      outcomeDao.insertOutcome(amendOutcome);
    }
  }

  private static Map<String, List<LicenceView>> licenceViewMap = new ConcurrentHashMap<>();

  public static synchronized List<LicenceView> getLicenceViews(String userId) {
    if (licenceViewMap.get(userId) == null) {
      List<LicenceView> licenceViews = new ArrayList<>();
      for (int i = 0; i < 20; i++) {
        String customerId = i % 2 == 0 ? TestUtil.wrapCustomerId(userId, COMPANY_ID_ONE) : TestUtil.wrapCustomerId(userId, COMPANY_ID_TWO);
        LicenceView.Status status = LicenceView.Status.values()[i % LicenceView.Status.values().length];
        List<String> destinationList = i % 2 == 0 ? Collections.singletonList(GERMANY) : Arrays.asList(ICELAND, FRANCE);
        Long issueTimestamp = time(2015, 3, 1 + i, 15, 10);
        Long expiryTimestamp = status == LicenceView.Status.ACTIVE ? time(2019, 3, 1 + i, 15, 10) : time(2016, 3, 1 + i, 15, 10);
        LicenceView licenceView = new LicenceView();
        licenceView.setLicenceRef(RandomIdUtil.randomNumber("REF-"));
        licenceView.setOriginalAppId(RandomIdUtil.randomNumber("APP"));
        licenceView.setOriginalExporterRef(RandomIdUtil.randomNumber("EREF-"));
        licenceView.setCustomerId(customerId);
        licenceView.setSiteId(SITE_ID);
        licenceView.setType(Type.SIEL);
        licenceView.setSubType(null);
        licenceView.setIssueDate(TimeUtil.toLocalDate(issueTimestamp));
        licenceView.setExpiryDate(TimeUtil.toLocalDate(expiryTimestamp));
        licenceView.setStatus(status);
        licenceView.setCountryList(destinationList);
        licenceView.setExternalDocumentUrl("");
        licenceViews.add(licenceView);
      }
      licenceViewMap.put(userId, licenceViews);
    }
    return licenceViewMap.get(userId);
  }

}
