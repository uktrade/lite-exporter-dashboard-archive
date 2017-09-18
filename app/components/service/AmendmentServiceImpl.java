package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.RandomUtil;
import models.Amendment;
import models.File;
import models.User;
import models.enums.DraftType;
import models.enums.RoutingKey;

import java.time.Instant;
import java.util.List;

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
  public void insertAmendment(String sentBy, String appId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(appId, files);
    Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), sentBy, message, attachments);
    amendmentDao.insertAmendment(amendment);
    draftDao.deleteDraft(appId, DraftType.AMENDMENT);
    messagePublisher.sendMessage(RoutingKey.AMEND_CREATE, amendment);
  }

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT);
    files.addAll(draftAttachments);
    return files;
  }

}
