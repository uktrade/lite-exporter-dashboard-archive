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

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.ApplicationDao;
import components.dao.BacklogDao;
import components.dao.CaseDetailsDao;
import components.dao.DraftFileDao;
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
import components.service.TimeService;
import components.service.UserPermissionService;
import components.util.RandomIdUtil;
import models.AmendmentRequest;
import models.Application;
import models.CaseDetails;
import models.Document;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.OutcomeDocument;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class TestDataServiceImpl implements TestDataService {

  static final String OFFICER_ID = "3";
  static final String OTHER_OFFICER_ID = "300";

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

  private static final List<DocumentType> ISSUE_DOCUMENT_TYPES = Arrays.asList(DocumentType.ISSUE_COVER_LETTER,
      DocumentType.ISSUE_LICENCE_DOCUMENT,
      DocumentType.ISSUE_REFUSE_DOCUMENT,
      DocumentType.ISSUE_NLR_DOCUMENT,
      DocumentType.ISSUE_AMENDMENT_LETTER);
  private static final List<String> ISSUE_DOCUMENT_FILE_NAMES = Arrays.asList("CoverLetter.pdf",
      "LicenceDocument.pdf",
      "RefuseDocument.pdf",
      "NlrDocument.pdf",
      "AmendmentLetter.pdf");

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
  private final DraftFileDao draftFileDao;
  private final OutcomeDao outcomeDao;
  private final NotificationDao notificationDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final ReadDao readDao;
  private final UserPermissionService userPermissionService;
  private final CaseDetailsDao caseDetailsDao;
  private final BacklogDao backlogDao;
  private final TimeService timeService;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao, StatusUpdateDao statusUpdateDao, RfiReplyDao rfiReplyDao,
                             ApplicationDao applicationDao, WithdrawalRequestDao withdrawalRequestDao,
                             AmendmentRequestDao amendmentRequestDao, DraftFileDao draftFileDao, OutcomeDao outcomeDao,
                             NotificationDao notificationDao, WithdrawalRejectionDao withdrawalRejectionDao,
                             WithdrawalApprovalDao withdrawalApprovalDao, RfiWithdrawalDao rfiWithdrawalDao,
                             ReadDao readDao, UserPermissionService userPermissionService,
                             CaseDetailsDao caseDetailsDao, BacklogDao backlogDao, TimeService timeService) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiReplyDao = rfiReplyDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentRequestDao = amendmentRequestDao;
    this.draftFileDao = draftFileDao;
    this.outcomeDao = outcomeDao;
    this.notificationDao = notificationDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.readDao = readDao;
    this.userPermissionService = userPermissionService;
    this.caseDetailsDao = caseDetailsDao;
    this.backlogDao = backlogDao;
    this.timeService = timeService;
  }

  @Override
  public void deleteAllUsersAndInsertStartData() {
    deleteAllData();
    insertTwoCompanies("1002", "1001", "SAR101", "SAR102", "SAR101_SITE101",
        Arrays.asList("1001", "1002", "1003"));
    insertOneCompany("2001", "SAR201", "SAR201_SITE201", Arrays.asList("2001", "2002", "2003"));
    insertUserTestingApplicant("3001", "3002", "SAR301", "SAR302", "SAR301_SITE301",
        Arrays.asList("3001", "3002", "3003"));
  }

  private void insertOneCompany(String userId, String companyId, String siteId, List<String> recipients) {
    createCompletedApplications(userId, companyId, siteId, recipients);
    createNoCaseOfficerApplication(userId, companyId, siteId);
    createAdvancedApplication(userId, companyId, siteId);
    createWithdrawnOrStoppedApplication(userId, companyId, siteId, false, recipients);
    createWithdrawnOrStoppedApplication(userId, companyId, siteId, true, recipients);
  }

  private void insertTwoCompanies(String userId, String otherUserId, String companyIdOne, String companyIdTwo,
                                  String siteId, List<String> recipients) {
    createApplications(userId, companyIdOne, siteId, recipients);
    createOtherUserApplications(otherUserId, companyIdOne, siteId, recipients);
    createAdvancedApplication(userId, companyIdTwo, siteId);
  }

  private void insertUserTestingApplicant(String userId, String otherUserId, String companyIdOne, String companyIdTwo,
                                          String siteId, List<String> recipients) {
    // Draft application, not submitted
    for (int i = 0; i < 1; i++) {
      String appId = appId();
      Application app = new Application(appId,
          companyIdOne,
          userId,
          timeService.time(2017, 1, 3, 10, 20),
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      insert(app);
    }
    // Submitted application, in progress, with RFI and message “apologies for the delay”
    for (int i = 0; i < 1; i++) {
      String appId = appId();
      Long submittedTimestamp = timeService.time(2017, 11, 6, 10, 20);
      String caseReference = RandomIdUtil.caseReference();
      Application app = new Application(appId,
          companyIdOne,
          userId,
          timeService.time(2017, 11, 3, 10, 20),
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          submittedTimestamp);
      insert(app, caseDetails);
      StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
          app.getId(),
          StatusType.INITIAL_CHECKS,
          timeService.time(2017, 11, 10, 10, 20));
      statusUpdateDao.insertStatusUpdate(initialChecks);
      StatusUpdate technicalAssessment = new StatusUpdate(statusUpdateId(),
          app.getId(),
          StatusType.TECHNICAL_ASSESSMENT,
          timeService.time(2017, 12, 15, 10, 20));
      statusUpdateDao.insertStatusUpdate(technicalAssessment);
      Notification delayNotification = new Notification(
          delayNotificationId(),
          caseReference,
          NotificationType.DELAY,
          null,
          timeService.time(2017, 12, 9, 13, 20),
          recipients,
          "We're sorry to inform you that your application has been delayed.",
          null);
      notificationDao.insertNotification(delayNotification);
      String rfiId = rfiId();
      Rfi rfi = new Rfi(rfiId,
          caseReference,
          timeService.time(2017, 12, 20, 10, 20),
          timeService.time(2018, 1, 22, 10, 20),
          OFFICER_ID,
          recipients,
          "Please answer this rfi.");
      rfiDao.insertRfi(rfi);
    }
    // Submitted application, completed, with outcome documents
    // Application amended after submission, (amendments in progress)
    // Application amended after submission, (amendments in stopped)
    for (int k = 0; k < 3; k++) {
      String appId = appId();
      String caseReference = RandomIdUtil.caseReference();
      String createdByUserId = k == 2 ? otherUserId : userId;
      Application application = new Application(appId,
          companyIdTwo,
          createdByUserId,
          timeService.time(2016, 4, 3, 3 + k, 3),
          CONSIGNEE_COUNTRIES_FOUR,
          END_USER_COUNTRIES_FOUR,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          timeService.time(2016, 5, 3, 3, 3));
      insert(application, caseDetails);
      List<StatusType> statusTypes = Arrays.asList(StatusType.INITIAL_CHECKS,
          StatusType.TECHNICAL_ASSESSMENT,
          StatusType.LU_PROCESSING,
          StatusType.WITH_OGD,
          StatusType.FINAL_ASSESSMENT,
          StatusType.COMPLETE);
      for (int i = 0; i < statusTypes.size(); i++) {
        StatusType statusType = statusTypes.get(i);
        Long createdTimestamp = timeService.time(2016, 5, 3 + 2 * i, 3 + i, 3 + i);
        StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(), appId, statusType, createdTimestamp);
        statusUpdateDao.insertStatusUpdate(statusUpdate);
      }
      long outcomeCreatedTimestamp = timeService.time(2016, 5, 13, 13, 17);
      List<OutcomeDocument> issueOutcomeDocuments = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = ISSUE_DOCUMENT_TYPES.get(j);
        OutcomeDocument outcomeDocument = new OutcomeDocument(documentId(),
            documentType,
            LICENCE_REFERENCES.get(j),
            ISSUE_DOCUMENT_FILE_NAMES.get(j),
            "#");
        issueOutcomeDocuments.add(outcomeDocument);
      }
      Outcome outcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, recipients, outcomeCreatedTimestamp, issueOutcomeDocuments);
      outcomeDao.insertOutcome(outcome);
      if (k == 0) {
        long informCreatedTimestamp = timeService.time(2016, 5, 13, 13, 17);
        Document document = new Document(fileId(), "Licence required inform", "#");
        Notification notification = new Notification(informNotificationId(), caseReference, NotificationType.INFORM, OFFICER_ID, informCreatedTimestamp, recipients, null, document);
        notificationDao.insertNotification(notification);
      } else if (k == 1) {
        long createdTimestamp = timeService.time(2017, 12, 2, 2, 2);
        String caseReference2 = RandomIdUtil.caseReference();
        CaseDetails caseDetails2 = new CaseDetails(appId, caseReference2, OFFICER_ID, createdTimestamp);
        caseDetailsDao.insert(caseDetails2);
        Rfi rfi = new Rfi(RandomIdUtil.rfiId(),
            caseReference2,
            timeService.time(2017, 12, 15, 2, 2),
            timeService.time(2018, 1, 15, 2, 2),
            OFFICER_ID,
            recipients,
            "Please answer this rfi");
        rfiDao.insertRfi(rfi);
      } else {
        long createdTimestamp = timeService.time(2017, 2, 2, 2, 2);
        String caseReference2 = RandomIdUtil.caseReference();
        CaseDetails caseDetails2 = new CaseDetails(appId, caseReference2, OFFICER_ID, createdTimestamp);
        caseDetailsDao.insert(caseDetails2);
        Notification stopNotification = new Notification(stopNotificationId(),
            caseReference2,
            NotificationType.STOP,
            TestDataServiceImpl.OFFICER_ID,
            timeService.time(2017, 3, 1, 14, 30),
            recipients,
            "We have had to stop your amendment.",
            null);
        notificationDao.insertNotification(stopNotification);
      }
    }
  }

  @Override
  public void deleteCurrentUser(String userId) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    List<Application> applications = applicationDao.getApplicationsByCustomerIdsAndUserId(customerIds, userId);

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
    rfiIds.forEach(rfiId -> draftFileDao.deleteDraftFiles(rfiId, DraftType.RFI_REPLY));
    appIds.forEach(appId -> draftFileDao.deleteDraftFiles(appId, DraftType.AMENDMENT_OR_WITHDRAWAL));
    caseReferences.forEach(notificationDao::deleteNotifications);
    readDao.deleteAllReadDataByUserId(userId);
    caseReferences.forEach(rfiDao::deleteRfiByCaseReference);
    caseReferences.forEach(caseDetailsDao::deleteCaseDetails);
    appIds.forEach(applicationDao::deleteApplication);
  }

  @Override
  public void deleteAllData() {
    statusUpdateDao.deleteAllStatusUpdates();
    rfiReplyDao.deleteAllRfiReplies();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    withdrawalRejectionDao.deleteAllWithdrawalRejections();
    withdrawalApprovalDao.deleteAllWithdrawalApprovals();
    amendmentRequestDao.deleteAllAmendmentRequests();
    draftFileDao.deleteAllDraftFiles();
    outcomeDao.deleteAllOutcomes();
    notificationDao.deleteAllNotifications();
    rfiWithdrawalDao.deleteAllRfiWithdrawals();
    readDao.deleteAllReadData();
    rfiDao.deleteAllRfiData();
    caseDetailsDao.deleteAllCaseDetails();
    applicationDao.deleteAllApplications();
    backlogDao.deleteAllBacklogMessages();
  }

  private void createApplications(String userId, String companyId, String siteId, List<String> recipients) {
    for (int i = 0; i < 20; i++) {
      String appId = appId();
      Long submittedTimestamp = timeService.time(2017, 4, 6 + i, i, i);
      String caseReference = RandomIdUtil.caseReference();
      Application app = new Application(appId,
          companyId,
          userId,
          timeService.time(2017, 3, 3 + i, i, i),
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          submittedTimestamp);
      insert(app, caseDetails);
      StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
          app.getId(),
          StatusType.INITIAL_CHECKS,
          timeService.time(2017, 4, 4 + i, i, i));
      statusUpdateDao.insertStatusUpdate(initialChecks);
      String rfiId = rfiId();
      Rfi rfi = new Rfi(rfiId,
          caseReference,
          timeService.time(2017, 4, 5 + i, i, i),
          timeService.time(2017, 5, 5 + i, i, i),
          OFFICER_ID,
          recipients,
          "Please answer this rfi.");
      rfiDao.insertRfi(rfi);
      String rfiTwoId = rfiId();
      Rfi rfiTwo = new Rfi(rfiTwoId,
          caseReference,
          timeService.time(2017, 6, 5 + i, i, i),
          timeService.time(2017, 7, 5 + i, i, i),
          OFFICER_ID,
          recipients,
          "Please also answer this rfi.");
      rfiDao.insertRfi(rfiTwo);
      if (i % 3 != 0) {
        RfiReply rfiReply = new RfiReply();
        rfiReply.setId(rfiReplyId());
        rfiReply.setRfiId(rfiId);
        rfiReply.setCreatedByUserId(userId);
        rfiReply.setCreatedTimestamp(timeService.time(2017, 4, 5 + i, i, i));
        rfiReply.setMessage("This is a reply.");
        rfiReply.setAttachments(new ArrayList<>());
        rfiReplyDao.insertRfiReply(rfiReply);
        RfiWithdrawal rfiWithdrawal = new RfiWithdrawal(rfiWithdrawalId(),
            rfiTwoId,
            OFFICER_ID,
            timeService.time(2017, 8, 5 + i, i, i),
            recipients,
            "This rfi has been withdrawn.");
        rfiWithdrawalDao.insertRfiWithdrawal(rfiWithdrawal);

      }
      if (i % 3 == 0) {
        Document document = new Document(fileId(), "Inform letter", "#");
        Notification notification = new Notification(informNotificationId(),
            caseReference,
            NotificationType.INFORM,
            OFFICER_ID,
            timeService.time(2017, 5, 1 + i, 2, 3),
            recipients,
            null,
            document);
        notificationDao.insertNotification(notification);
      }
    }
  }

  private void createOtherUserApplications(String userId, String companyId, String siteId, List<String> recipients) {
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = appId();
      Application app = new Application(appId,
          companyId,
          userId,
          timeService.time(2017, 1, 3 + i, i, i),
          CONSIGNEE_COUNTRIES,
          END_USER_COUNTRIES,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      CaseDetails caseDetails = new CaseDetails(appId,
          RandomIdUtil.caseReference(),
          OFFICER_ID,
          timeService.time(2017, 1, 4 + i, i, i));
      insert(app, caseDetails);
    }
    // Create application with inform notice
    String appId = appId();
    String caseReference = RandomIdUtil.caseReference();
    Application application = new Application(appId,
        companyId,
        userId,
        timeService.time(2017, 1, 7, 1, 1),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        OFFICER_ID,
        siteId);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        timeService.time(2017, 1, 8, 1, 1));
    insert(application, caseDetails);
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        application.getId(),
        StatusType.INITIAL_CHECKS,
        timeService.time(2017, 8, 3, 0, 0));
    statusUpdateDao.insertStatusUpdate(initialChecks);
    Document document = new Document(fileId(), "Inform letter", "#");
    Notification notification = new Notification(informNotificationId(),
        caseReference,
        NotificationType.INFORM,
        OFFICER_ID,
        timeService.time(2017, 9, 1, 2, 3),
        recipients,
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

  private void createWithdrawnOrStoppedApplication(String userId, String companyId, String siteId, boolean stopped,
                                                   List<String> recipients) {
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
        companyId,
        userId,
        timeService.time(2013, 11, 4, 13, 10),
        consigneeCountries,
        endUserCountries,
        getApplicantReference(),
        OFFICER_ID,
        siteId);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        timeService.time(2013, 11, 4, 14, 10));
    insert(application, caseDetails);
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        timeService.time(2013, 12, 5, 3, 3));
    statusUpdateDao.insertStatusUpdate(initialChecks);
    StatusUpdate technicalAssessment = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.TECHNICAL_ASSESSMENT,
        timeService.time(2015, 5, 6, 13, 10));
    statusUpdateDao.insertStatusUpdate(technicalAssessment);

    AmendmentRequest amendmentRequest = new AmendmentRequest();
    amendmentRequest.setId(amendmentId());
    amendmentRequest.setAppId(appId);
    amendmentRequest.setCreatedByUserId(userId);
    amendmentRequest.setCreatedTimestamp(timeService.time(2014, 11, 5, 14, 17));
    amendmentRequest.setAttachments(new ArrayList<>());
    amendmentRequest.setMessage("This is an amendment.");
    amendmentRequestDao.insertAmendmentRequest(amendmentRequest);

    for (int i = 0; i < 4; i++) {
      WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
      withdrawalRequest.setId(withdrawalRequestId());
      withdrawalRequest.setAppId(appId);
      withdrawalRequest.setCreatedByUserId(userId);
      withdrawalRequest.setCreatedTimestamp(timeService.time(2015, 1 + 2 * i, 5, 13, 10));
      withdrawalRequest.setMessage("This is a withdrawal request.");
      withdrawalRequest.setAttachments(new ArrayList<>());
      withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
      if (i != 3) {
        Long createdTimestamp = timeService.time(2015, 1 + 2 * i + 1, 5, 13, 10);
        WithdrawalRejection withdrawalRejection = new WithdrawalRejection(withdrawalRejectionId(),
            appId,
            userId,
            createdTimestamp,
            recipients,
            "");
        withdrawalRejectionDao.insertWithdrawalRejection(withdrawalRejection);
      }
    }

    Notification delayNotification = new Notification(
        delayNotificationId(),
        caseReference,
        NotificationType.DELAY,
        null,
        timeService.time(2016, 1, 1, 13, 20),
        recipients,
        "We're sorry to inform you that your application has been delayed.",
        null);
    notificationDao.insertNotification(delayNotification);

    if (stopped) {
      Notification stopNotification = new Notification(
          stopNotificationId(),
          caseReference,
          NotificationType.STOP,
          TestDataServiceImpl.OFFICER_ID,
          timeService.time(2017, 1, 1, 14, 30),
          recipients,
          "We have had to stop your application.",
          null);
      notificationDao.insertNotification(stopNotification);
    } else {
      WithdrawalApproval withdrawalApproval = new WithdrawalApproval(withdrawalApprovalId(),
          appId,
          OFFICER_ID,
          timeService.time(2017, 1, 5, 13, 10),
          recipients,
          null);
      withdrawalApprovalDao.insertWithdrawalApproval(withdrawalApproval);
    }
  }

  private void createAdvancedApplication(String userId, String companyId, String siteId) {
    String appId = appId();
    String caseReference = RandomIdUtil.caseReference();
    String rfiId = rfiId();
    Application application = new Application(appId,
        companyId,
        userId,
        timeService.time(2016, 11, 4, 13, 10),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        OFFICER_ID,
        siteId);
    CaseDetails caseDetails = new CaseDetails(appId,
        caseReference,
        OFFICER_ID,
        timeService.time(2016, 11, 4, 14, 10));
    insert(application, caseDetails);
    createStatusUpdateTestData(appId).forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData(caseReference, rfiId).forEach(rfiDao::insertRfi);
    rfiReplyDao.insertRfiReply(createRfiReplyTestData(userId, rfiId));
  }

  private RfiReply createRfiReplyTestData(String userId, String rfiId) {
    RfiReply rfiReply = new RfiReply();
    rfiReply.setId(rfiReplyId());
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedByUserId(userId);
    rfiReply.setCreatedTimestamp(timeService.time(2017, 5, 13, 16, 10));
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
        timeService.time(2017, 1, 2, 13, 30),
        timeService.time(2017, 2, 2, 13, 30),
        OFFICER_ID,
        new ArrayList<>(),
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(rfiId,
        caseReference,
        timeService.time(2017, 2, 5, 10, 10),
        timeService.time(2017, 3, 12, 16, 10),
        OFFICER_ID,
        new ArrayList<>(),
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(rfiId(),
        caseReference,
        timeService.time(2017, 4, 5, 10, 10),
        timeService.time(2017, 5, 12, 16, 10),
        OFFICER_ID,
        new ArrayList<>(),
        "This is some rfi message.");
    Rfi rfiFour = new Rfi(rfiId(),
        caseReference,
        timeService.time(2017, 7, 5, 10, 10),
        timeService.time(2018, 8, 5, 10, 10),
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
        timeService.time(2017, 1, 2, 13, 30));
    StatusUpdate technicalAssessment = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.TECHNICAL_ASSESSMENT,
        timeService.time(2017, 5, 5, 0, 0));
    StatusUpdate licenseUnitProcessing = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.LU_PROCESSING,
        timeService.time(2017, 7, 5, 0, 0));
    List<StatusUpdate> statusUpdates = new ArrayList<>();
    statusUpdates.add(initialChecks);
    statusUpdates.add(technicalAssessment);
    statusUpdates.add(licenseUnitProcessing);
    return statusUpdates;
  }

  private void createNoCaseOfficerApplication(String userId, String companyId, String siteId) {
    String appId = appId();
    Application application = new Application(appId,
        companyId,
        userId,
        timeService.time(2016, 11, 3, 3, 3),
        CONSIGNEE_COUNTRIES,
        END_USER_COUNTRIES,
        getApplicantReference(),
        null,
        siteId);
    CaseDetails caseDetails = new CaseDetails(appId,
        RandomIdUtil.caseReference(),
        OFFICER_ID,
        timeService.time(2016, 12, 4, 3, 3));
    insert(application, caseDetails);
    StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        timeService.time(2016, 12, 5, 3, 3));
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void createCompletedApplications(String userId, String companyId, String siteId, List<String> recipients) {
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
          companyId,
          userId,
          timeService.time(2015, 3, 3, 3 + k, 3),
          consigneeCountries,
          endUserCountries,
          getApplicantReference(),
          OFFICER_ID,
          siteId);
      CaseDetails caseDetails = new CaseDetails(appId,
          caseReference,
          OFFICER_ID,
          timeService.time(2015, 4, 3, 3, 3));
      insert(application, caseDetails);
      List<StatusType> statusTypes = Arrays.asList(StatusType.INITIAL_CHECKS,
          StatusType.TECHNICAL_ASSESSMENT,
          StatusType.LU_PROCESSING,
          StatusType.WITH_OGD,
          StatusType.FINAL_ASSESSMENT,
          StatusType.COMPLETE);
      for (int i = 0; i < statusTypes.size(); i++) {
        StatusType statusType = statusTypes.get(i);
        Long createdTimestamp = timeService.time(2016, 5, 3 + i, 3 + i, 3 + i);
        StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(), appId, statusType, createdTimestamp);
        statusUpdateDao.insertStatusUpdate(statusUpdate);
      }

      long outcomeCreatedTimestamp = timeService.time(2016, 7, 10, 13, 17);
      List<OutcomeDocument> issueOutcomeDocuments = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = ISSUE_DOCUMENT_TYPES.get(j);
        OutcomeDocument outcomeDocument = new OutcomeDocument(documentId(),
            documentType,
            LICENCE_REFERENCES.get(j),
            UUID.randomUUID().toString() + ".pdf",
            "#");
        issueOutcomeDocuments.add(outcomeDocument);
      }
      Outcome outcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, recipients, outcomeCreatedTimestamp, issueOutcomeDocuments);
      outcomeDao.insertOutcome(outcome);

      if (k > 4) {
        insertCompletedCase(appId, recipients);
      }
      if (k == 1 || k == 6) {
        insertCase(appId, false, false, false, false, recipients);
      } else if (k == 2 || k == 7) {
        insertCase(appId, false, true, true, false, recipients);
      } else if (k == 3 || k == 8) {
        insertCase(appId, true, false, true, false, recipients);
      } else if (k == 4 || k == 9) {
        insertCase(appId, false, false, true, true, recipients);
      }

      for (int i = 0; i < 3; i++) {
        long informCreatedTimestamp = timeService.time(2016, 8 + i, 3 + i, 3 + i, 3 + i);
        Document document = new Document(fileId(), "Licence required inform letter number " + (i + 1), "#");
        Notification notification = new Notification(informNotificationId(), caseReference, NotificationType.INFORM, OFFICER_ID, informCreatedTimestamp, recipients, null, document);
        notificationDao.insertNotification(notification);
      }
    }
  }

  private void insertCompletedCase(String appId, List<String> recipients) {
    long createdTimestamp = timeService.time(2016, 12, 20, 20, 20);
    String caseReference = RandomIdUtil.caseReference();
    CaseDetails caseDetails = new CaseDetails(appId, caseReference, OFFICER_ID, createdTimestamp);
    caseDetailsDao.insert(caseDetails);
    List<OutcomeDocument> outcomeDocuments = new ArrayList<>();
    for (int j = 0; j < 4; j++) {
      DocumentType documentType = AMEND_DOCUMENT_TYPES.get(j);
      OutcomeDocument outcomeDocument = new OutcomeDocument(documentId(),
          documentType,
          LICENCE_REFERENCES.get(j),
          UUID.randomUUID().toString() + ".pdf",
          "#");
      outcomeDocuments.add(outcomeDocument);
    }
    long outcomeCreatedTimestamp = timeService.time(2016, 12, 22, 13, 17);
    Outcome amendOutcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, recipients, outcomeCreatedTimestamp, outcomeDocuments);
    outcomeDao.insertOutcome(amendOutcome);
  }

  private void insertCase(String appId, boolean hasOutcome, boolean hasRfi, boolean hasInformLetter,
                          boolean isStopped, List<String> recipients) {
    long createdTimestamp = timeService.time(2017, 2, 2, 2, 2);
    String caseReference = RandomIdUtil.caseReference();
    CaseDetails caseDetails = new CaseDetails(appId, caseReference, OFFICER_ID, createdTimestamp);
    caseDetailsDao.insert(caseDetails);
    if (hasRfi) {
      for (int i = 0; i < 2; i++) {
        Rfi rfi = new Rfi(RandomIdUtil.rfiId(),
            caseReference,
            timeService.time(2017, 3 + i, 2, 2, 2),
            timeService.time(2018, 3 + i, 2, 2, 2),
            OFFICER_ID,
            recipients,
            "Please answer this rfi");
        rfiDao.insertRfi(rfi);
      }
    }
    if (hasInformLetter) {
      long informCreatedTimestamp = timeService.time(2017, 4, 4, 4, 4);
      Document document = new Document(fileId(), "Licence required inform letter number 4", "#");
      Notification notification = new Notification(informNotificationId(), caseReference, NotificationType.INFORM, OFFICER_ID, informCreatedTimestamp, recipients, null, document);
      notificationDao.insertNotification(notification);
    }
    if (isStopped) {
      Notification stopNotification = new Notification(stopNotificationId(),
          caseReference,
          NotificationType.STOP,
          TestDataServiceImpl.OFFICER_ID,
          timeService.time(2017, 5, 1, 14, 30),
          recipients,
          "We have had to stop your amendment.",
          null);
      notificationDao.insertNotification(stopNotification);
    }
    if (hasOutcome) {
      List<OutcomeDocument> outcomeDocuments = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = AMEND_DOCUMENT_TYPES.get(j);
        OutcomeDocument outcomeDocument = new OutcomeDocument(documentId(),
            documentType,
            LICENCE_REFERENCES.get(j),
            ISSUE_DOCUMENT_FILE_NAMES.get(j),
            "#");
        outcomeDocuments.add(outcomeDocument);
      }
      long outcomeCreatedTimestamp = timeService.time(2017, 3, 10, 13, 17);
      Outcome amendOutcome = new Outcome(outcomeId(), caseReference, OFFICER_ID, recipients, outcomeCreatedTimestamp, outcomeDocuments);
      outcomeDao.insertOutcome(amendOutcome);
    }
  }

  private void insert(Application application, CaseDetails caseDetails) {
    insert(application);
    caseDetailsDao.insert(caseDetails);
  }

  private void insert(Application application) {
    applicationDao.insert(application.getId(), application.getCreatedByUserId(), application.getCreatedTimestamp());
    applicationDao.updateApplicantReference(application.getId(), application.getApplicantReference());
    applicationDao.updateCountries(application.getId(), application.getConsigneeCountries(), application.getEndUserCountries());
    applicationDao.updateCustomerId(application.getId(), application.getCustomerId());
    applicationDao.updateSiteId(application.getId(), application.getSiteId());
  }

}
