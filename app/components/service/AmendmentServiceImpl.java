package components.service;

import static components.util.RandomIdUtil.amendmentId;

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.DraftFileDao;
import components.message.MessagePublisher;
import controllers.routes;
import models.AmendmentRequest;
import models.Attachment;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.AmendmentMessage;
import uk.gov.bis.lite.exporterdashboard.api.DashboardDocument;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class AmendmentServiceImpl implements AmendmentService {

  private final AmendmentRequestDao amendmentRequestDao;
  private final MessagePublisher messagePublisher;
  private final DraftFileDao draftFileDao;

  @Inject
  public AmendmentServiceImpl(AmendmentRequestDao amendmentRequestDao, MessagePublisher messagePublisher, DraftFileDao draftFileDao) {
    this.amendmentRequestDao = amendmentRequestDao;
    this.messagePublisher = messagePublisher;
    this.draftFileDao = draftFileDao;
  }

  @Override
  public void insertAmendment(String createdByUserId, String appId, String message) {
    List<Attachment> attachments = draftFileDao.getAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);

    AmendmentRequest amendmentRequest = new AmendmentRequest();
    amendmentRequest.setId(amendmentId());
    amendmentRequest.setAppId(appId);
    amendmentRequest.setCreatedByUserId(createdByUserId);
    amendmentRequest.setCreatedTimestamp(Instant.now().toEpochMilli());
    amendmentRequest.setMessage(message);
    amendmentRequest.setAttachments(attachments);

    amendmentRequestDao.insertAmendmentRequest(amendmentRequest);
    draftFileDao.deleteDraftFiles(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    messagePublisher.sendMessage(RoutingKey.AMENDMENT_CREATE, getAmendmentMessage(amendmentRequest));
  }

  private AmendmentMessage getAmendmentMessage(AmendmentRequest amendmentRequest) {
    AmendmentMessage amendmentMessage = new AmendmentMessage();
    amendmentMessage.setId(amendmentRequest.getId());
    amendmentMessage.setAppId(amendmentRequest.getAppId());
    amendmentMessage.setCreatedByUserId(amendmentRequest.getCreatedByUserId());
    amendmentMessage.setCreatedTimestamp(amendmentRequest.getCreatedTimestamp());
    amendmentMessage.setMessage(amendmentRequest.getMessage());
    amendmentMessage.setAttachments(getDashboardDocuments(amendmentRequest.getAppId(), amendmentRequest.getAttachments()));
    return amendmentMessage;
  }

  private List<DashboardDocument> getDashboardDocuments(String appId, List<Attachment> attachments) {
    return attachments.stream()
        .map(attachment -> {
          String url = routes.DownloadController.getAmendmentOrWithdrawalAttachment(appId, attachment.getId()).toString();
          DashboardDocument dashboardDocument = new DashboardDocument();
          dashboardDocument.setId(attachment.getId());
          dashboardDocument.setFilename(attachment.getFilename());
          dashboardDocument.setUrl(url);
          return dashboardDocument;
        }).collect(Collectors.toList());
  }

}
