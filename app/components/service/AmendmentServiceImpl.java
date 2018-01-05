package components.service;

import static components.util.RandomIdUtil.amendmentId;

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.DraftFileDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.MessageUtil;
import java.time.Instant;
import java.util.List;
import models.AmendmentRequest;
import models.File;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.AmendmentMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

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
  public void insertAmendment(String createdByUserId, String appId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(appId, files);

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

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftFileDao.getDraftFiles(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    files.addAll(draftAttachments);
    return files;
  }

  private AmendmentMessage getAmendmentMessage(AmendmentRequest amendmentRequest) {
    AmendmentMessage amendmentMessage = new AmendmentMessage();
    amendmentMessage.setId(amendmentRequest.getId());
    amendmentMessage.setAppId(amendmentRequest.getAppId());
    amendmentMessage.setCreatedByUserId(amendmentRequest.getCreatedByUserId());
    amendmentMessage.setCreatedTimestamp(amendmentRequest.getCreatedTimestamp());
    amendmentMessage.setMessage(amendmentRequest.getMessage());
    amendmentMessage.setAttachments(MessageUtil.getDashboardDocuments(amendmentRequest.getAttachments()));
    return amendmentMessage;
  }

}
