package components.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import components.dao.ApplicationDao;
import components.dao.CaseDetailsDao;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.RfiDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.exceptions.ValidationException;
import components.service.EscapeHtmlService;
import components.util.EnumUtil;
import components.util.RandomIdUtil;
import models.CaseDetails;
import models.Document;
import models.Notification;
import models.NotificationType;
import models.Outcome;
import models.OutcomeDocument;
import models.Rfi;
import models.RfiWithdrawal;
import models.StatusUpdate;
import models.WithdrawalApproval;
import models.WithdrawalRejection;
import models.enums.DocumentType;
import models.enums.StatusType;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.sielapp.api.SielApplicantRefUpdate;
import uk.gov.bis.lite.sielapp.api.SielCreate;
import uk.gov.bis.lite.sielapp.api.SielDelete;
import uk.gov.bis.lite.sielapp.api.SielDestinationsUpdate;
import uk.gov.bis.lite.sielapp.api.SielLicenseeUpdate;
import uk.gov.bis.lite.sielapp.api.SielSiteUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardCaseCreated;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardCaseStatusUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardMessageDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationDelay;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationInform;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardNotificationStop;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOfficerUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeAmend;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeDocument;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardOutcomeIssue;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiDeadlineUpdate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardRfiWithdrawalCreate;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalAccept;
import uk.gov.bis.lite.spirerelay.model.queue.publish.dashboard.DashboardWithdrawalReject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Validation;
import javax.validation.Validator;

public class MessageHandlerImpl implements MessageHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandlerImpl.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  private final NotificationDao notificationDao;
  private final StatusUpdateDao statusUpdateDao;
  private final RfiDao rfiDao;
  private final RfiWithdrawalDao rfiWithdrawalDao;
  private final OutcomeDao outcomeDao;
  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;
  private final CaseDetailsDao caseDetailsDao;
  private final ApplicationDao applicationDao;
  private final EscapeHtmlService escapeHtmlService;

  @Inject
  public MessageHandlerImpl(
      RfiDao rfiDao,
      StatusUpdateDao statusUpdateDao,
      NotificationDao notificationDao,
      RfiWithdrawalDao rfiWithdrawalDao,
      OutcomeDao outcomeDao,
      WithdrawalRejectionDao withdrawalRejectionDao,
      WithdrawalApprovalDao withdrawalApprovalDao,
      CaseDetailsDao caseDetailsDao,
      ApplicationDao applicationDao,
      EscapeHtmlService escapeHtmlService) {
    this.rfiDao = rfiDao;
    this.statusUpdateDao = statusUpdateDao;
    this.notificationDao = notificationDao;
    this.rfiWithdrawalDao = rfiWithdrawalDao;
    this.outcomeDao = outcomeDao;
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
    this.caseDetailsDao = caseDetailsDao;
    this.applicationDao = applicationDao;
    this.escapeHtmlService = escapeHtmlService;
  }

  @Override
  public boolean handleMessage(String routingKey, String message) {
    ConsumerRoutingKey consumerRoutingKey = EnumUtil.parse(routingKey, ConsumerRoutingKey.class);
    if (consumerRoutingKey == null) {
      LOGGER.error("Unknown routing key {}", routingKey);
      return false;
    }
    LOGGER.info("Received queue message {} with routing key {}", message, consumerRoutingKey);
    try {
      switch (consumerRoutingKey) {
        case RFI:
          insertRfi(message);
          break;
        case STATUS_UPDATE:
          insertStatusUpdate(message);
          break;
        case DELAY_NOTIFICATION:
          insertDelayNotification(message);
          break;
        case STOP_NOTIFICATION:
          insertStopNotification(message);
          break;
        case INFORM_NOTIFICATION:
          insertInformNotification(message);
          break;
        case RFI_WITHDRAWAL:
          insertRfiWithdrawal(message);
          break;
        case OUTCOME_ISSUE:
          insertOutcomeIssue(message);
          break;
        case OUTCOME_AMEND:
          insertOutcomeAmend(message);
          break;
        case WITHDRAWAL_REJECTION:
          insertWithdrawalRejection(message);
          break;
        case WITHDRAWAL_ACCEPT:
          insertWithdrawalAccept(message);
          break;
        case CASE_CREATE:
          insertCaseCreate(message);
          break;
        case OFFICER_UPDATE:
          insertCaseOfficerUpdate(message);
          break;
        case RFI_DEADLINE_UPDATE:
          insertRfiDeadlineUpdate(message);
          break;
        case SIEL_CREATE:
          insertSielCreate(message);
          break;
        case SIEL_UPDATE_APPLICANT_REF:
          insertSielUpdateApplicantRef(message);
          break;
        case SIEL_UPDATE_LICENSEE:
          insertSielUpdateLicensee(message);
          break;
        case SIEL_UPDATE_SITE:
          insertSielUpdateSite(message);
          break;
        case SIEL_UPDATE_DESTINATIONS:
          insertSielUpdateDestinations(message);
          break;
        case SIEL_DELETE:
          insertSielDelete(message);
          break;
        default:
          throw new ValidationException("Unknown routing key " + consumerRoutingKey);
      }
    } catch (Exception exception) {
      String errorMessage = String.format("Unable to handle delivery of message %s with routing key %s", message, consumerRoutingKey);
      LOGGER.error(errorMessage, exception);
      return false;
    }
    return true;
  }

  private void insertRfi(String message) {
    DashboardRfiCreate dashboardRfiCreate = parse(message, DashboardRfiCreate.class);
    String escaped = escapeHtmlService.escape(dashboardRfiCreate.getMessage());
    Rfi rfi = new Rfi(dashboardRfiCreate.getId(),
        dashboardRfiCreate.getCaseRef(),
        dashboardRfiCreate.getCreatedTimestamp(),
        dashboardRfiCreate.getDeadlineTimestamp(),
        dashboardRfiCreate.getCreatedByUserId(),
        dashboardRfiCreate.getRecipientUserIds(),
        escaped);
    validate(rfi);
    rfiDao.insertRfi(rfi);
  }

  private void insertStatusUpdate(String message) {
    DashboardCaseStatusUpdate dashboardCaseStatusUpdate = parse(message, DashboardCaseStatusUpdate.class);
    if (dashboardCaseStatusUpdate.getStatusCode() == null) {
      throw new ValidationException("statusCode of DashboardCaseStatusUpdate cannot be null.");
    }
    StatusType statusType = EnumUtil.parse(dashboardCaseStatusUpdate.getStatusCode().toString(), StatusType.class);
    StatusUpdate statusUpdate = new StatusUpdate(RandomIdUtil.statusUpdateId(),
        dashboardCaseStatusUpdate.getAppId(),
        statusType,
        dashboardCaseStatusUpdate.getCreatedTimestamp());
    validate(statusUpdate);
    statusUpdateDao.insertStatusUpdate(statusUpdate);
  }

  private void insertDelayNotification(String message) {
    DashboardNotificationDelay dashboardNotificationDelay = parse(message, DashboardNotificationDelay.class);
    String escaped = escapeHtmlService.escape(dashboardNotificationDelay.getMessage());
    Notification notification = new Notification(dashboardNotificationDelay.getId(),
        dashboardNotificationDelay.getCaseRef(),
        NotificationType.DELAY,
        null,
        dashboardNotificationDelay.getCreatedTimestamp(),
        dashboardNotificationDelay.getRecipientUserIds(),
        escaped,
        null);
    validate(notification, "createdByUserId", "document");
    notificationDao.insertNotification(notification);
  }

  private void insertStopNotification(String message) {
    DashboardNotificationStop dashboardNotificationStop = parse(message, DashboardNotificationStop.class);
    String escaped = escapeHtmlService.escape(dashboardNotificationStop.getMessage());
    Notification notification = new Notification(dashboardNotificationStop.getId(),
        dashboardNotificationStop.getCaseRef(),
        NotificationType.STOP,
        dashboardNotificationStop.getCreatedByUserId(),
        dashboardNotificationStop.getCreatedTimestamp(),
        dashboardNotificationStop.getRecipientUserIds(),
        escaped,
        null);
    validate(notification, "document");
    notificationDao.insertNotification(notification);
  }

  private void insertInformNotification(String message) {
    DashboardNotificationInform dashboardNotificationInform = parse(message, DashboardNotificationInform.class);
    DashboardMessageDocument dashboardMessageDocument = dashboardNotificationInform.getDocument();
    if (dashboardMessageDocument == null) {
      throw new ValidationException("Document of DashboardNotificationInform cannot be null.");
    }
    Document document = new Document(dashboardMessageDocument.getId(), dashboardMessageDocument.getFilename(), dashboardMessageDocument.getUrl());
    validate(document);
    Notification notification = new Notification(dashboardNotificationInform.getId(),
        dashboardNotificationInform.getCaseRef(),
        NotificationType.INFORM,
        dashboardNotificationInform.getCreatedByUserId(),
        dashboardNotificationInform.getCreatedTimestamp(),
        dashboardNotificationInform.getRecipientUserIds(),
        null,
        document);
    validate(notification, "message");
    notificationDao.insertNotification(notification);
  }

  private void insertRfiWithdrawal(String message) {
    DashboardRfiWithdrawalCreate dashboardRfiWithdrawalCreate = parse(message, DashboardRfiWithdrawalCreate.class);
    RfiWithdrawal rfiWithdrawal = new RfiWithdrawal(RandomIdUtil.rfiWithdrawalId(),
        dashboardRfiWithdrawalCreate.getRfiId(),
        dashboardRfiWithdrawalCreate.getCreatedByUserId(),
        dashboardRfiWithdrawalCreate.getCreatedTimestamp(),
        dashboardRfiWithdrawalCreate.getRecipientUserIds(),
        dashboardRfiWithdrawalCreate.getMessage());
    validate(rfiWithdrawal);
    rfiWithdrawalDao.insertRfiWithdrawal(rfiWithdrawal);
  }

  private void insertOutcomeIssue(String message) {
    DashboardOutcomeIssue dashboardOutcomeIssue = parse(message, DashboardOutcomeIssue.class);
    List<OutcomeDocument> outcomeDocuments = parseOutcomeDocuments(dashboardOutcomeIssue.getDocuments(), "ISSUE_");
    Outcome outcome = new Outcome(dashboardOutcomeIssue.getId(),
        dashboardOutcomeIssue.getCaseRef(),
        dashboardOutcomeIssue.getCreatedByUserId(),
        dashboardOutcomeIssue.getRecipientUserIds(),
        dashboardOutcomeIssue.getCreatedTimestamp(),
        outcomeDocuments);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private void insertOutcomeAmend(String message) {
    DashboardOutcomeAmend dashboardOutcomeAmend = parse(message, DashboardOutcomeAmend.class);
    List<OutcomeDocument> outcomeDocuments = parseOutcomeDocuments(dashboardOutcomeAmend.getDocuments(), "AMENDMENT_");
    Outcome outcome = new Outcome(dashboardOutcomeAmend.getId(),
        dashboardOutcomeAmend.getCaseRef(),
        dashboardOutcomeAmend.getCreatedByUserId(),
        dashboardOutcomeAmend.getRecipientUserIds(),
        dashboardOutcomeAmend.getCreatedTimestamp(),
        outcomeDocuments);
    validate(outcome);
    outcomeDao.insertOutcome(outcome);
  }

  private List<OutcomeDocument> parseOutcomeDocuments(List<DashboardOutcomeDocument> dashboardOutcomeDocuments,
                                                      String prefix) {
    if (CollectionUtils.isEmpty(dashboardOutcomeDocuments) || dashboardOutcomeDocuments.contains(null)) {
      throw new ValidationException("Documents cannot be empty or contain null.");
    }
    List<OutcomeDocument> outcomeDocuments = dashboardOutcomeDocuments.stream()
        .map(dashboardOutcomeDocument -> {
          DocumentType documentType = EnumUtil.parse(prefix + dashboardOutcomeDocument.getDocumentType(), DocumentType.class);
          return new OutcomeDocument(dashboardOutcomeDocument.getId(),
              documentType,
              dashboardOutcomeDocument.getLicenceRef(),
              dashboardOutcomeDocument.getFilename(),
              dashboardOutcomeDocument.getUrl());
        }).collect(Collectors.toList());
    outcomeDocuments.forEach(outcomeDocument -> {
      if (outcomeDocument.getDocumentType() == DocumentType.AMENDMENT_LICENCE_DOCUMENT || outcomeDocument.getDocumentType() == DocumentType.ISSUE_LICENCE_DOCUMENT) {
        validate(outcomeDocument);
      } else {
        validate(outcomeDocument, "licenceRef");
      }
    });
    return outcomeDocuments;
  }

  private void insertWithdrawalRejection(String message) {
    DashboardWithdrawalReject dashboardWithdrawalReject = parse(message, DashboardWithdrawalReject.class);
    String escaped = escapeHtmlService.escape(dashboardWithdrawalReject.getMessage());
    WithdrawalRejection withdrawalRejection = new WithdrawalRejection(RandomIdUtil.withdrawalRejectionId(),
        dashboardWithdrawalReject.getAppId(),
        dashboardWithdrawalReject.getCreatedByUserId(),
        System.currentTimeMillis(),
        dashboardWithdrawalReject.getRecipientUserIds(),
        escaped);
    validate(withdrawalRejection);
    withdrawalRejectionDao.insertWithdrawalRejection(withdrawalRejection);
  }

  private void insertWithdrawalAccept(String message) {
    DashboardWithdrawalAccept dashboardWithdrawalAccept = parse(message, DashboardWithdrawalAccept.class);
    String escaped = escapeHtmlService.escape(dashboardWithdrawalAccept.getMessage());
    WithdrawalApproval withdrawalApproval = new WithdrawalApproval(dashboardWithdrawalAccept.getId(),
        dashboardWithdrawalAccept.getAppId(),
        dashboardWithdrawalAccept.getCreatedByUserId(),
        dashboardWithdrawalAccept.getCreatedTimestamp(),
        dashboardWithdrawalAccept.getRecipientUserIds(),
        escaped);
    validate(withdrawalApproval);
    withdrawalApprovalDao.insertWithdrawalApproval(withdrawalApproval);
  }

  private void insertCaseCreate(String message) {
    DashboardCaseCreated dashboardCaseCreated = parse(message, DashboardCaseCreated.class);
    CaseDetails caseDetails = new CaseDetails(dashboardCaseCreated.getAppId(),
        dashboardCaseCreated.getCaseRef(),
        dashboardCaseCreated.getCreatedByUserId(),
        dashboardCaseCreated.getCreatedTimestamp());
    validate(caseDetails);
    caseDetailsDao.insert(caseDetails);
  }

  private void insertSielCreate(String message) {
    SielCreate sielCreate = parse(message, SielCreate.class);
    validate(sielCreate);
    applicationDao.insert(sielCreate.getAppId(), sielCreate.getCreatedByUserId(), sielCreate.getCreatedTimestamp());
  }

  private void insertSielUpdateApplicantRef(String message) {
    SielApplicantRefUpdate sielApplicantRefUpdate = parse(message, SielApplicantRefUpdate.class);
    validate(sielApplicantRefUpdate);
    verifyApplicationExists(sielApplicantRefUpdate.getAppId());
    applicationDao.updateApplicantReference(sielApplicantRefUpdate.getAppId(), sielApplicantRefUpdate.getApplicantRef());
  }

  private void insertSielUpdateLicensee(String message) {
    SielLicenseeUpdate sielLicenseeUpdate = parse(message, SielLicenseeUpdate.class);
    validate(sielLicenseeUpdate);
    verifyApplicationExists(sielLicenseeUpdate.getAppId());
    applicationDao.updateCustomerId(sielLicenseeUpdate.getAppId(), sielLicenseeUpdate.getLicenseeId());
  }

  private void insertSielUpdateSite(String message) {
    SielSiteUpdate sielSiteUpdate = parse(message, SielSiteUpdate.class);
    validate(sielSiteUpdate);
    verifyApplicationExists(sielSiteUpdate.getAppId());
    applicationDao.updateSiteId(sielSiteUpdate.getAppId(), sielSiteUpdate.getSiteId());
  }

  private void insertSielUpdateDestinations(String message) {
    SielDestinationsUpdate sielDestinationsUpdate = parse(message, SielDestinationsUpdate.class);
    validate(sielDestinationsUpdate);
    verifyApplicationExists(sielDestinationsUpdate.getAppId());
    applicationDao.updateCountries(sielDestinationsUpdate.getAppId(),
        Collections.singletonList(sielDestinationsUpdate.getConsigneeCountry()),
        sielDestinationsUpdate.getEndUserCountries());
  }

  private void insertSielDelete(String message) {
    SielDelete sielDelete = parse(message, SielDelete.class);
    validate(sielDelete);
    verifyApplicationExists(sielDelete.getAppId());
    applicationDao.deleteApplication(sielDelete.getAppId());
  }

  private void insertCaseOfficerUpdate(String message) {
    DashboardOfficerUpdate dashboardOfficerUpdate = parse(message, DashboardOfficerUpdate.class);
    validate(dashboardOfficerUpdate);
    applicationDao.updateCaseOfficerId(dashboardOfficerUpdate.getAppId(), dashboardOfficerUpdate.getCaseOfficerId());
  }

  private void insertRfiDeadlineUpdate(String message) {
    DashboardRfiDeadlineUpdate dashboardRfiDeadlineUpdate = parse(message, DashboardRfiDeadlineUpdate.class);
    validate(dashboardRfiDeadlineUpdate);
    rfiDao.updateDeadline(dashboardRfiDeadlineUpdate.getRfiId(), dashboardRfiDeadlineUpdate.getUpdatedDeadlineTimestamp());
  }

  private void verifyApplicationExists(String appId) {
    if (applicationDao.getApplication(appId) == null) {
      String errorMessage = String.format("Unable to insert message with appId %s since no such application exists.", appId);
      throw new ValidationException(errorMessage);
    }
  }

  private <T> T parse(String message, Class<T> clazz) {
    try {
      T object = MAPPER.readValue(message, clazz);
      if (object == null) {
        throw new ValidationException("Unable to parse message into class " + clazz.getSimpleName());
      } else {
        return object;
      }
    } catch (IOException ioe) {
      throw new ValidationException("Unable to parse message into class " + clazz.getSimpleName(), ioe);
    }
  }

  private void validate(Object object, String... excludeFields) {
    Set<String> exclude = Stream.of(excludeFields).collect(Collectors.toSet());
    List<String> errorMessages = VALIDATOR.validate(object).stream()
        .filter(cv -> !exclude.contains(cv.getPropertyPath().toString()))
        .map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage())
        .collect(Collectors.toList());
    if (!errorMessages.isEmpty()) {
      String errorMessage = String.join(", ", errorMessages);
      throw new ValidationException(errorMessage);
    }
  }

}
