package components.service.test;

import static components.util.RandomIdUtil.amendmentId;
import static components.util.RandomIdUtil.appId;
import static components.util.RandomIdUtil.delayNotificationId;
import static components.util.RandomIdUtil.fileId;
import static components.util.RandomIdUtil.informNotificationId;
import static components.util.RandomIdUtil.outcomeId;
import static components.util.RandomIdUtil.rfiId;
import static components.util.RandomIdUtil.rfiReplyId;
import static components.util.RandomIdUtil.rfiWithdrawalId;
import static components.util.RandomIdUtil.sielId;
import static components.util.RandomIdUtil.statusUpdateId;
import static components.util.RandomIdUtil.stopNotificationId;
import static components.util.RandomIdUtil.withdrawalApprovalId;
import static components.util.RandomIdUtil.withdrawalRejectionId;
import static components.util.RandomIdUtil.withdrawalRequestId;
import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.AmendmentDao;
import components.dao.ApplicationDao;
import components.dao.DraftDao;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.ReadDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.RfiWithdrawalDao;
import components.dao.SielDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import models.Application;
import models.Document;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.Rfi;
import models.RfiWithdrawal;
import models.Siel;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.enums.DocumentType;
import models.enums.DraftType;
import models.enums.RfiStatus;
import models.enums.SielStatus;
import models.enums.StatusType;
import org.apache.commons.lang3.RandomUtils;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.exporterdashboard.api.Amendment;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

public class TestDataServiceImpl implements TestDataService {

  public static final String APPLICANT_ID = "24492";
  public static final String APPLICANT_TWO_ID = "24493";
  public static final String APPLICANT_THREE_ID = "24494";
  public static final List<String> RECIPIENTS = Arrays.asList(APPLICANT_ID, APPLICANT_TWO_ID, APPLICANT_THREE_ID);
  public static final String ADMIN_ID = "1";

  public static final String OTHER_APPLICANT_ID = "2";
  public static final String OFFICER_ID = "3";

  private static final String APP_QUEUE_ID = "app_queue";

  private static final String GERMANY = "Germany";
  private static final String ICELAND = "Iceland";
  private static final String FRANCE = "France";

  private static final String COMPANY_ID_ONE = "SAR1";
  private static final String COMPANY_ID_TWO = "SAR2";
  private static final String COMPANY_ID_THREE = "SAR3";

  private final RfiDao rfiDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiReplyDao rfiReplyDao;
  private final ApplicationDao applicationDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final AmendmentDao amendmentDao;
  private final DraftDao draftDao;
  private final SielDao sielDao;
  private final OutcomeDao outcomeDao;
  private final CustomerServiceClient customerServiceClient;
  private final NotificationDao notificationDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final ReadDao readDao;

  @Inject
  public TestDataServiceImpl(RfiDao rfiDao,
                             StatusUpdateDao statusUpdateDao,
                             RfiReplyDao rfiReplyDao,
                             ApplicationDao applicationDao,
                             WithdrawalRequestDao withdrawalRequestDao,
                             AmendmentDao amendmentDao,
                             DraftDao draftDao,
                             SielDao sielDao,
                             OutcomeDao outcomeDao,
                             CustomerServiceClient customerServiceClient,
                             NotificationDao notificationDao,
                             WithdrawalRejectionDao withdrawalRejectionDao,
                             WithdrawalApprovalDao withdrawalApprovalDao,
                             RfiWithdrawalDao rfiWithdrawalDao,
                             ReadDao readDao) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.rfiReplyDao = rfiReplyDao;
    this.applicationDao = applicationDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.amendmentDao = amendmentDao;
    this.draftDao = draftDao;
    this.sielDao = sielDao;
    this.outcomeDao = outcomeDao;
    this.customerServiceClient = customerServiceClient;
    this.notificationDao = notificationDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.readDao = readDao;
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
    createCompleteApplication(userId, false);
    createCompleteApplication(userId, true);
    createNoCaseOfficerApplication(userId);
    createAdvancedApplication(userId);
    createEmptyQueueApplication(userId);
    createSiels(userId);
    createWithdrawnOrStoppedApplication(userId, false);
    createWithdrawnOrStoppedApplication(userId, true);
  }

  @Override
  public void insertTwoCompanies(String userId) {
    createApplications(userId);
    createSecondUserApplications(userId);
    createAdvancedApplication(userId);
    createSiels(userId);
  }

  @Override
  public void insertOtherUserApplications(String userId) {
    createSecondUserApplications(userId);
  }

  @Override
  public void deleteCurrentUser(String userId) {
    List<String> customerIds = customerServiceClient.getCustomers(userId).stream()
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
    List<String> appIds = applicationDao.getApplications(customerIds).stream()
        .map(Application::getId)
        .collect(Collectors.toList());
    appIds.forEach(outcomeDao::deleteOutcomesByAppId);
    appIds.forEach(statusUpdateDao::deleteStatusUpdatesByAppId);
    appIds.forEach(withdrawalRequestDao::deleteWithdrawalRequestsByAppId);
    appIds.forEach(withdrawalRejectionDao::deleteWithdrawalRejectionsByAppId);
    appIds.forEach(withdrawalApprovalDao::deleteWithdrawalApprovalsByAppId);
    appIds.forEach(amendmentDao::deleteAmendmentsByAppId);
    List<String> rfiIds = rfiDao.getRfiList(appIds).stream()
        .map(Rfi::getId)
        .collect(Collectors.toList());
    appIds.forEach(rfiDao::deleteRfiListByAppId);
    rfiIds.forEach(rfiReplyDao::deleteRfiRepliesByRfiId);
    rfiIds.forEach(rfiWithdrawalDao::deleteRfiWithdrawalByRfiId);
    customerIds.forEach(sielDao::deleteSielsByCustomerId);
    appIds.forEach(applicationDao::deleteApplication);
    rfiIds.forEach(rfiId -> draftDao.deleteDraft(rfiId, DraftType.RFI_REPLY));
    appIds.forEach(appId -> draftDao.deleteDraft(appId, DraftType.WITHDRAWAL));
    appIds.forEach(appId -> draftDao.deleteDraft(appId, DraftType.AMENDMENT));
    appIds.forEach(notificationDao::deleteNotificationsByAppId);
    readDao.deleteAllReadDataByUserId(userId);
  }

  @Override
  public void deleteAllUsers() {
    applicationDao.deleteAllApplications();
    statusUpdateDao.deleteAllStatusUpdates();
    rfiDao.deleteAllRfiData();
    rfiReplyDao.deleteAllRfiReplies();
    withdrawalRequestDao.deleteAllWithdrawalRequests();
    withdrawalRejectionDao.deleteAllWithdrawalRejections();
    withdrawalApprovalDao.deleteAllWithdrawalApprovals();
    amendmentDao.deleteAllAmendments();
    draftDao.deleteAllDrafts();
    sielDao.deleteAllSiels();
    outcomeDao.deleteAllOutcomes();
    notificationDao.deleteAllNotifications();
    rfiWithdrawalDao.deleteAllRfiWithdrawals();
    readDao.deleteAllReadData();
  }

  // Siel Ogel
  // Admin: N N
  // Applicant11: Y N
  // Applicant2: Y Y
  // Applicant3: N Y
  private void createSiels(String userId) {
    for (int i = 1; i < 22; i++) {
      String companyId = i % 2 == 0 ? wrapCustomerId(userId, COMPANY_ID_ONE) : wrapCustomerId(userId, COMPANY_ID_TWO);
      SielStatus sielStatus = SielStatus.values()[i % SielStatus.values().length];
      List<String> destinationList = i % 2 == 0 ? Collections.singletonList(GERMANY) : Arrays.asList(ICELAND, FRANCE);
      Long expiryTimestamp = sielStatus == SielStatus.ACTIVE ? time(2017, 3, i, 15, 10) : time(2016, 3, i, 15, 10);
      Siel siel = new Siel(sielId(),
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

  private void createEmptyQueueApplication(String userId) {
    Application application = new Application(userId + "_" + APP_QUEUE_ID,
        wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2015, 1, 1, 1, 1),
        time(2015, 2, 1, 1, 1),
        Collections.singletonList(GERMANY),
        getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(application);
  }

  private void createApplications(String userId) {
    for (int i = 0; i < 20; i++) {
      String appId = appId();
      boolean isDraft = i % 4 == 0;
      Long submittedTimestamp = isDraft ? null : time(2017, 4, 3 + i, i, i);
      String caseReference = isDraft ? null : randomNumber("ECO");
      Application app = new Application(appId,
          wrapCustomerId(userId, COMPANY_ID_ONE),
          userId,
          time(2017, 3, 3 + i, i, i),
          submittedTimestamp,
          Collections.singletonList(GERMANY),
          getApplicantReference(),
          caseReference,
          OFFICER_ID);
      applicationDao.insert(app);
      if (!isDraft) {
        StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
            app.getId(),
            StatusType.INITIAL_CHECKS,
            time(2017, 4, 4 + i, i, i));
        statusUpdateDao.insertStatusUpdate(initialChecks);
        String rfiId = rfiId();
        Rfi rfi = new Rfi(rfiId,
            appId,
            RfiStatus.ACTIVE,
            time(2017, 4, 5 + i, i, i),
            time(2017, 5, 5 + i, i, i),
            OFFICER_ID,
            RECIPIENTS,
            "Please answer this rfi.");
        rfiDao.insertRfi(rfi);
        String rfiTwoId = rfiId();
        Rfi rfiTwo = new Rfi(rfiTwoId,
            appId,
            RfiStatus.ACTIVE,
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
          File document = new File();
          document.setId("FIL");
          document.setUrl("#");
          document.setFilename("Inform letter");
          Notification notification = new Notification(informNotificationId(),
              appId,
              NotificationType.INFORM,
              OFFICER_ID,
              time(2017, 5, 1 + i, 2, 3),
              RECIPIENTS,
              "",
              document);
          notificationDao.insertNotification(notification);
        }
      }
    }
  }

  private void createSecondUserApplications(String userId) {
    // create applications by other applicant
    for (int i = 0; i < 4; i++) {
      String appId = appId();
      Application app = new Application(appId,
          wrapCustomerId(userId, COMPANY_ID_ONE),
          OTHER_APPLICANT_ID,
          time(2017, 1, 3 + i, i, i),
          null,
          Collections.singletonList(FRANCE),
          getApplicantReference(),
          null,
          OFFICER_ID);
      applicationDao.insert(app);
    }
    // Create application with inform notice
    String appId = appId();
    Application app = new Application(appId,
        wrapCustomerId(userId, COMPANY_ID_ONE),
        OTHER_APPLICANT_ID,
        time(2017, 1, 7, 1, 1),
        time(2017, 1, 8, 1, 1),
        Collections.singletonList(FRANCE),
        getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(app);
    StatusUpdate initialChecks = new StatusUpdate(statusUpdateId(),
        app.getId(),
        StatusType.INITIAL_CHECKS,
        time(2017, 8, 3, 0, 0));
    statusUpdateDao.insertStatusUpdate(initialChecks);
    File document = new File();
    document.setId("FIL");
    document.setUrl("#");
    document.setFilename("Inform letter");
    Notification notification = new Notification(informNotificationId(),
        appId,
        NotificationType.INFORM,
        OFFICER_ID,
        time(2017, 9, 1, 2, 3),
        RECIPIENTS,
        "",
        document);
    notificationDao.insertNotification(notification);
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

  private void createWithdrawnOrStoppedApplication(String userId, boolean stopped) {
    String appId = appId();
    Application application = new Application(appId,
        wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2013, 11, 4, 13, 10),
        time(2013, 11, 4, 14, 10),
        Arrays.asList(GERMANY, ICELAND, FRANCE),
        getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(application);

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

    Amendment amendment = new Amendment();
    amendment.setId(amendmentId());
    amendment.setAppId(appId);
    amendment.setCreatedByUserId(userId);
    amendment.setCreatedTimestamp(time(2014, 11, 5, 14, 17));
    amendment.setAttachments(new ArrayList<>());
    amendment.setMessage("This is an amendment.");
    amendmentDao.insertAmendment(amendment);

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
        appId,
        NotificationType.DELAY,
        TestDataServiceImpl.OFFICER_ID,
        time(2016, 1, 1, 13, 20),
        RECIPIENTS,
        "We're sorry to inform you that your application has been delayed.",
        null);
    notificationDao.insertNotification(delayNotification);

    if (stopped) {
      Notification stopNotification = new Notification(
          stopNotificationId(),
          appId,
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
          "");
      withdrawalApprovalDao.insertWithdrawalApproval(withdrawalApproval);
    }
  }

  private void createAdvancedApplication(String userId) {
    String appId = appId();
    String rfiId = rfiId();
    Application application = new Application(appId,
        wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2016, 11, 4, 13, 10),
        time(2016, 11, 4, 14, 10),
        Arrays.asList(GERMANY, ICELAND, FRANCE),
        getApplicantReference(),
        randomNumber("ECO"),
        OFFICER_ID);
    applicationDao.insert(application);
    createStatusUpdateTestData(appId).forEach(statusUpdateDao::insertStatusUpdate);
    createRfiTestData(appId, rfiId).forEach(rfiDao::insertRfi);
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

  private List<Rfi> createRfiTestData(String appId, String rfiId) {
    Rfi rfi = new Rfi(rfiId(),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 1, 2, 13, 30),
        time(2017, 2, 2, 13, 30),
        OFFICER_ID,
        RECIPIENTS,
        "Please reply to this rfi message.");
    Rfi rfiTwo = new Rfi(rfiId,
        appId,
        RfiStatus.ACTIVE,
        time(2017, 2, 5, 10, 10),
        time(2017, 3, 12, 16, 10),
        OFFICER_ID,
        RECIPIENTS,
        "<p>We note from your application that you have rated all 8 line items as ML10a and that these items are used in production and maintenance of civil and/or military aircraft.</p>"
            + "<p>Would you please provide the make/model of aircraft for which each of the 8 line items on your application was originally designed.</p>"
            + "<p>Than you for your help in this matter.</p>");
    Rfi rfiThree = new Rfi(rfiId(),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 4, 5, 10, 10),
        time(2017, 5, 12, 16, 10),
        OFFICER_ID,
        RECIPIENTS,
        "This is some rfi message.");
    Rfi rfiFour = new Rfi(rfiId(),
        appId,
        RfiStatus.ACTIVE,
        time(2017, 7, 5, 10, 10),
        time(2018, 8, 5, 10, 10),
        OFFICER_ID,
        RECIPIENTS,
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
        wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
        time(2016, 11, 3, 3, 3),
        time(2016, 12, 4, 3, 3),
        Collections.singletonList(FRANCE), getApplicantReference(),
        randomNumber("ECO"),
        null);
    applicationDao.insert(application);
    StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(),
        appId,
        StatusType.INITIAL_CHECKS,
        time(2016, 12, 5, 3, 3));
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void createCompleteApplication(String userId, boolean hasAmendments) {
    String appId = appId();
    Application application = new Application(appId,
        wrapCustomerId(userId, COMPANY_ID_TWO),
        userId,
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
      Long createdTimestamp = time(2017, 5, 3 + i, 3 + i, 3 + i);
      StatusUpdate statusUpdate = new StatusUpdate(statusUpdateId(), appId, statusType, createdTimestamp);
      statusUpdateDao.insertStatusUpdate(statusUpdate);
    }

    String letter = "Cover letter";
    String licence = "Licence SIE2017/000001 granted for some or all of your items";
    String refusal = "Letter explaining the licence refusal of some or all of your items";
    String nlr = "Letter confirming that no licence is required for some or all of your items";
    List<String> licenceRefs = Arrays.asList(letter, licence, refusal, nlr);

    List<DocumentType> issueDocumentTypes = Arrays.asList(DocumentType.ISSUE_LETTER, DocumentType.ISSUE_LICENCE, DocumentType.ISSUE_REFUSAL, DocumentType.ISSUE_NLR);
    List<DocumentType> amendDocumentTypes = Arrays.asList(DocumentType.AMEND_LETTER, DocumentType.AMEND_LICENCE, DocumentType.AMEND_REFUSAL, DocumentType.AMEND_NLR);

    int max = hasAmendments ? 4 : 1;

    for (int i = 0; i < max; i++) {
      long createdTimestamp = time(2010 + i, 2 + i, 10 + i, 13, 17);
      String outcomeId = outcomeId();
      Outcome outcome = new Outcome();
      outcome.setId(outcomeId);
      outcome.setAppId(appId);
      outcome.setCreatedTimestamp(createdTimestamp);
      outcome.setCreatedByUserId(userId);
      outcome.setRecipientUserIds(RECIPIENTS);
      List<Document> documents = new ArrayList<>();
      for (int j = 0; j < 4; j++) {
        DocumentType documentType = i == 0 ? issueDocumentTypes.get(j) : amendDocumentTypes.get(j);
        Document document = new Document();
        document.setDocumentType(documentType);
        document.setFilename(UUID.randomUUID().toString() + ".pdf");
        document.setLicenceRef(licenceRefs.get(j));
        document.setUrl("#");
        documents.add(document);
      }
      outcome.setDocuments(documents);
      outcomeDao.insertOutcome(outcome);
    }

    for (int i = 0; i < 3; i++) {
      long createdTimestamp = time(2017, 5 + i, 3 + i, 3 + i, 3 + i);
      File document = new File();
      document.setId(fileId());
      document.setFilename("Licence required inform letter number " + (i + 1));
      document.setUrl("#");
      Notification notification = new Notification(informNotificationId(), appId, NotificationType.INFORM, OFFICER_ID, createdTimestamp, RECIPIENTS, "", document);
      notificationDao.insertNotification(notification);
    }
  }

  private String randomNumber(String prefix) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < 12; i++) {
      stringBuilder.append(RandomUtils.nextInt(0, 9));
    }
    return prefix + stringBuilder.toString();
  }

  public static String wrapCustomerId(String userId, String customerId) {
    return userId + "_" + customerId;
  }

  public static String unwrapCustomerId(String customerId) {
    return customerId.replaceAll(".*_", "");
  }

}
