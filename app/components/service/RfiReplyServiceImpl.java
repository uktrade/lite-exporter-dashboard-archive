package components.service;

import static components.util.RandomIdUtil.rfiReplyId;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import java.time.Instant;
import java.util.List;
import models.enums.DraftType;
import models.enums.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

public class RfiReplyServiceImpl implements RfiReplyService {

  private final RfiReplyDao rfiReplyDao;
  private final DraftDao draftDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public RfiReplyServiceImpl(RfiReplyDao rfiReplyDao, DraftDao draftDao, MessagePublisher messagePublisher) {
    this.rfiReplyDao = rfiReplyDao;
    this.draftDao = draftDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void insertRfiReply(String createdByUserId, String rfiId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(rfiId, files);

    RfiReply rfiReply = new RfiReply();
    rfiReply.setId(rfiReplyId());
    rfiReply.setRfiId(rfiId);
    rfiReply.setCreatedByUserId(createdByUserId);
    rfiReply.setCreatedTimestamp(Instant.now().toEpochMilli());
    rfiReply.setMessage(message);
    rfiReply.setAttachments(attachments);

    rfiReplyDao.insertRfiReply(rfiReply);
    draftDao.deleteDraft(rfiId, DraftType.RFI_REPLY);
    messagePublisher.sendMessage(RoutingKey.RFI_REPLY, rfiReply);
  }

  private List<File> getAttachments(String rfiId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(rfiId, DraftType.RFI_REPLY);
    files.addAll(draftAttachments);
    return files;
  }

}
