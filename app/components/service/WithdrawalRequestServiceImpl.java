package components.service;

import static components.util.RandomIdUtil.withdrawalRequestId;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.WithdrawalRequestDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.MessageUtil;
import java.time.Instant;
import java.util.List;
import models.File;
import models.WithdrawalRequest;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestMessage;

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
    messagePublisher.sendMessage(RoutingKey.WITHDRAWAL_REQUEST_CREATE, getWithdrawalRequestMessage(withdrawalRequest));
  }

  private List<File> getAttachments(String appId, List<UploadFile> uploadFiles) {
    List<File> files = FileUtil.toFiles(uploadFiles);
    List<File> draftAttachments = draftDao.getDraftAttachments(appId, DraftType.WITHDRAWAL);
    files.addAll(draftAttachments);
    return files;
  }

  private WithdrawalRequestMessage getWithdrawalRequestMessage(WithdrawalRequest withdrawalRequest) {
    WithdrawalRequestMessage withdrawalRequestMessage = new WithdrawalRequestMessage();
    withdrawalRequestMessage.setId(withdrawalRequest.getId());
    withdrawalRequestMessage.setAppId(withdrawalRequest.getAppId());
    withdrawalRequestMessage.setCreatedByUserId(withdrawalRequest.getCreatedByUserId());
    withdrawalRequestMessage.setCreatedTimestamp(withdrawalRequest.getCreatedTimestamp());
    withdrawalRequestMessage.setMessage(withdrawalRequest.getMessage());
    withdrawalRequestMessage.setAttachments(MessageUtil.getFiles(withdrawalRequest.getAttachments()));
    return withdrawalRequestMessage;
  }

}
