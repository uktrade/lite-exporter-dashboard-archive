package components.service;

import static components.util.RandomIdUtil.rfiReplyId;

import com.google.inject.Inject;
import components.dao.DraftFileDao;
import components.dao.RfiReplyDao;
import components.message.MessagePublisher;
import controllers.routes;
import models.Attachment;
import models.RfiReply;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.DashboardDocument;
import uk.gov.bis.lite.exporterdashboard.api.RfiReplyMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class RfiReplyServiceImpl implements RfiReplyService {

  private final RfiReplyDao rfiReplyDao;
  private final DraftFileDao draftFileDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public RfiReplyServiceImpl(RfiReplyDao rfiReplyDao, DraftFileDao draftFileDao, MessagePublisher messagePublisher) {
    this.rfiReplyDao = rfiReplyDao;
    this.draftFileDao = draftFileDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void insertRfiReply(String createdByUserId, String appId, String rfiId, String message) {
    List<Attachment> attachments = draftFileDao.getAttachments(rfiId, DraftType.RFI_REPLY);

    RfiReply rfiReply = new RfiReply();
    rfiReply.setId(rfiReplyId());
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedByUserId(createdByUserId);
    rfiReply.setCreatedTimestamp(Instant.now().toEpochMilli());
    rfiReply.setMessage(message);
    rfiReply.setAttachments(attachments);

    rfiReplyDao.insertRfiReply(rfiReply);
    draftFileDao.deleteDraftFiles(rfiId, DraftType.RFI_REPLY);
    messagePublisher.sendMessage(RoutingKey.RFI_REPLY, getRfiReplyMessage(appId, rfiReply));
  }

  private RfiReplyMessage getRfiReplyMessage(String appId, RfiReply rfiReply) {
    RfiReplyMessage rfiReplyMessage = new RfiReplyMessage();
    rfiReplyMessage.setId(rfiReply.getId());
    rfiReplyMessage.setRfiId(rfiReply.getRfiId());
    rfiReplyMessage.setAppId(appId);
    rfiReplyMessage.setCreatedByUserId(rfiReply.getCreatedByUserId());
    rfiReplyMessage.setCreatedTimestamp(rfiReply.getCreatedTimestamp());
    rfiReplyMessage.setMessage(rfiReply.getMessage());
    rfiReplyMessage.setAttachments(getDashboardDocuments(appId, rfiReply.getRfiId(), rfiReply.getAttachments()));
    return rfiReplyMessage;
  }

  private List<DashboardDocument> getDashboardDocuments(String appId, String rfiId, List<Attachment> attachments) {
    return attachments.stream()
        .map(attachment -> {
          String url = routes.DownloadController.getRfiReplyAttachment(appId, rfiId, attachment.getId()).toString();
          DashboardDocument dashboardDocument = new DashboardDocument();
          dashboardDocument.setId(attachment.getId());
          dashboardDocument.setFilename(attachment.getFilename());
          dashboardDocument.setUrl(url);
          return dashboardDocument;
        }).collect(Collectors.toList());
  }

}
