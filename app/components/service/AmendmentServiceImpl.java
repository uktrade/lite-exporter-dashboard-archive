package components.service;

import static components.util.RandomIdUtil.amendmentId;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.MessageUtil;
import java.time.Instant;
import java.util.List;
import models.Amendment;
import models.File;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.AmendmentMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

public class AmendmentServiceImpl implements AmendmentService {

  private final AmendmentDao amendmentDao;
  private final MessagePublisher messagePublisher;
  private final DraftDao draftDao;

  @Inject
  public AmendmentServiceImpl(AmendmentDao amendmentDao, MessagePublisher messagePublisher, DraftDao draftDao) {
    this.amendmentDao = amendmentDao;
    this.messagePublisher = messagePublisher;
    this.draftDao = draftDao;
  }

  @Override
  public void insertAmendment(String createdByUserId, String appId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(appId, files);

    Amendment amendment = new Amendment();
    amendment.setId(amendmentId());
    amendment.setAppId(appId);
    amendment.setCreatedByUserId(createdByUserId);
    amendment.setCreatedTimestamp(Instant.now().toEpochMilli());
    amendment.setMessage(message);
    amendment.setAttachments(attachments);

    amendmentDao.insertAmendment(amendment);
    draftDao.deleteDraft(appId, DraftType.AMENDMENT);
    messagePublisher.sendMessage(RoutingKey.AMENDMENT_CREATE, getAmendmentMessage(amendment));
  }

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT);
    files.addAll(draftAttachments);
    return files;
  }

  private AmendmentMessage getAmendmentMessage(Amendment amendment) {
    AmendmentMessage amendmentMessage = new AmendmentMessage();
    amendmentMessage.setId(amendment.getId());
    amendmentMessage.setAppId(amendment.getAppId());
    amendmentMessage.setCreatedByUserId(amendment.getCreatedByUserId());
    amendmentMessage.setCreatedTimestamp(amendment.getCreatedTimestamp());
    amendmentMessage.setMessage(amendment.getMessage());
    amendmentMessage.setAttachments(MessageUtil.getFiles(amendment.getAttachments()));
    return amendmentMessage;
  }

}
