package components.service;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiResponseDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import models.File;
import models.RfiResponse;
import models.enums.DraftType;
import models.enums.RoutingKey;

import java.time.Instant;
import java.util.List;

public class RfiResponseServiceImpl implements RfiResponseService {

  private final RfiResponseDao rfiResponseDao;
  private final DraftDao draftDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public RfiResponseServiceImpl(RfiResponseDao rfiResponseDao, DraftDao draftDao, MessagePublisher messagePublisher) {
    this.rfiResponseDao = rfiResponseDao;
    this.draftDao = draftDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void insertRfiResponse(String sentBy, String rfiId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(rfiId, files);
    RfiResponse rfiResponse = new RfiResponse(rfiId, sentBy, Instant.now().toEpochMilli(), message, attachments);
    rfiResponseDao.insertRfiResponse(rfiResponse);
    draftDao.deleteDraft(rfiId, DraftType.RFI_RESPONSE);
    messagePublisher.sendMessage(RoutingKey.RFI_REPLY, rfiResponse);
  }

  private List<File> getAttachments(String rfiId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(rfiId, DraftType.RFI_RESPONSE);
    files.addAll(draftAttachments);
    return files;
  }

}
