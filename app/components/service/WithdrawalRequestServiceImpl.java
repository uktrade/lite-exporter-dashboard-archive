package components.service;

import static components.util.RandomIdUtil.withdrawalRequestId;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.WithdrawalRequestDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import java.time.Instant;
import java.util.List;
import models.enums.DraftType;
import models.enums.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {

  private final WithdrawalRequestDao withdrawalRequestDao;
  private final MessagePublisher messagePublisher;
  private final DraftDao draftDao;

  @Inject
  public WithdrawalRequestServiceImpl(WithdrawalRequestDao withdrawalRequestDao, MessagePublisher messagePublisher, DraftDao draftDao) {
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.messagePublisher = messagePublisher;
    this.draftDao = draftDao;
  }

  @Override
  public void insertWithdrawalRequest(String createdByUserId, String appId, String message, List<UploadFile> files) {
    List<File> attachments = getAttachments(appId, files);

    WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
    withdrawalRequest.setId(withdrawalRequestId());
    withdrawalRequest.setAppId(appId);
    withdrawalRequest.setCreatedByUserId(createdByUserId);
    withdrawalRequest.setCreatedTimestamp(Instant.now().toEpochMilli());
    withdrawalRequest.setMessage(message);
    withdrawalRequest.setAttachments(attachments);

    withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
    draftDao.deleteDraft(appId, DraftType.WITHDRAWAL);
    messagePublisher.sendMessage(RoutingKey.WITHDRAW_REQUEST_CREATE, withdrawalRequest);
  }

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.WITHDRAWAL);
    files.addAll(draftAttachments);
    return files;
  }

}
