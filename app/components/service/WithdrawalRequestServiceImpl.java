package components.service;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.WithdrawalRequestDao;
import components.message.MessagePublisher;
import components.upload.UploadFile;
import components.util.FileUtil;
import components.util.RandomUtil;
import models.File;
import models.User;
import models.enums.DraftType;
import models.enums.RoutingKey;

import java.time.Instant;
import java.util.List;

public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {

  private final UserService userService;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final MessagePublisher messagePublisher;
  private final DraftDao draftDao;

  @Inject
  public WithdrawalRequestServiceImpl(UserService userService, WithdrawalRequestDao withdrawalRequestDao, MessagePublisher messagePublisher, DraftDao draftDao) {
    this.userService = userService;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.messagePublisher = messagePublisher;
    this.draftDao = draftDao;
  }

  @Override
  public void insertWithdrawalRequest(String appId, String message, List<UploadFile> files) {
    User currentUser = userService.getCurrentUser();
    List<File> attachments = getAttachments(appId, files);
    models.WithdrawalRequest withdrawalRequest = new models.WithdrawalRequest(RandomUtil.random("WIT"),
        appId,
        Instant.now().toEpochMilli(),
        currentUser.getId(),
        message,
        attachments,
        null,
        null,
        null);
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
