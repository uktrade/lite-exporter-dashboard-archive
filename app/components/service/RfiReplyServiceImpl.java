package components.service;

import static components.util.RandomIdUtil.rfiReplyId;

import com.google.inject.Inject;
import components.dao.DraftFileDao;
import components.dao.RfiReplyDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.MessageUtil;
import java.time.Instant;
import java.util.List;
import models.File;
import models.RfiReply;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.RfiReplyMessage;

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
  public void insertRfiReply(String createdByUserId, String appId, String rfiId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(rfiId, files);

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

  private List<File> getAttachments(String rfiId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftFileDao.getDraftFiles(rfiId, DraftType.RFI_REPLY);
    files.addAll(draftAttachments);
    return files;
  }

  private RfiReplyMessage getRfiReplyMessage(String appId, RfiReply rfiReply) {
    RfiReplyMessage rfiReplyMessage = new RfiReplyMessage();
    rfiReplyMessage.setId(rfiReply.getId());
    rfiReplyMessage.setRfiId(rfiReply.getRfiId());
    rfiReplyMessage.setAppId(appId);
    rfiReplyMessage.setCreatedByUserId(rfiReply.getCreatedByUserId());
    rfiReplyMessage.setCreatedTimestamp(rfiReply.getCreatedTimestamp());
    rfiReplyMessage.setMessage(rfiReply.getMessage());
    rfiReplyMessage.setAttachments(MessageUtil.getDashboardDocuments(rfiReply.getAttachments()));
    return rfiReplyMessage;
  }

}
