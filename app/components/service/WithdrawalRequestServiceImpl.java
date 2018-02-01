package components.service;

import static components.util.RandomIdUtil.withdrawalRequestId;

import com.google.inject.Inject;
import components.dao.DraftFileDao;
import components.dao.WithdrawalRequestDao;
import components.message.MessagePublisher;
import controllers.routes;
import models.Attachment;
import models.WithdrawalRequest;
import models.enums.DraftType;
import uk.gov.bis.lite.exporterdashboard.api.DashboardDocument;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequestMessage;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {

  private final WithdrawalRequestDao withdrawalRequestDao;
  private final MessagePublisher messagePublisher;
  private final DraftFileDao draftFileDao;

  @Inject
  public WithdrawalRequestServiceImpl(WithdrawalRequestDao withdrawalRequestDao, MessagePublisher messagePublisher, DraftFileDao draftFileDao) {
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.messagePublisher = messagePublisher;
    this.draftFileDao = draftFileDao;
  }

  @Override
  public void insertWithdrawalRequest(String createdByUserId, String appId, String message) {
    List<Attachment> attachments = draftFileDao.getAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);

    WithdrawalRequest withdrawalRequest = new WithdrawalRequest();
    withdrawalRequest.setId(withdrawalRequestId());
    withdrawalRequest.setAppId(appId);
    withdrawalRequest.setCreatedByUserId(createdByUserId);
    withdrawalRequest.setCreatedTimestamp(Instant.now().toEpochMilli());
    withdrawalRequest.setMessage(message);
    withdrawalRequest.setAttachments(attachments);

    withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
    draftFileDao.deleteDraftFiles(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
    messagePublisher.sendMessage(RoutingKey.WITHDRAWAL_REQUEST_CREATE, getWithdrawalRequestMessage(withdrawalRequest));
  }

  private WithdrawalRequestMessage getWithdrawalRequestMessage(WithdrawalRequest withdrawalRequest) {
    WithdrawalRequestMessage withdrawalRequestMessage = new WithdrawalRequestMessage();
    withdrawalRequestMessage.setId(withdrawalRequest.getId());
    withdrawalRequestMessage.setAppId(withdrawalRequest.getAppId());
    withdrawalRequestMessage.setCreatedByUserId(withdrawalRequest.getCreatedByUserId());
    withdrawalRequestMessage.setCreatedTimestamp(withdrawalRequest.getCreatedTimestamp());
    withdrawalRequestMessage.setMessage(withdrawalRequest.getMessage());
    withdrawalRequestMessage.setAttachments(getDashboardDocuments(withdrawalRequest.getAppId(), withdrawalRequest.getAttachments()));
    return withdrawalRequestMessage;
  }

  private List<DashboardDocument> getDashboardDocuments(String appId, List<Attachment> attachments) {
    return attachments.stream()
        .map(attachment -> {
          String url = routes.DownloadController.getAmendmentOrWithdrawalAttachment(appId, attachment.getId()).toString();
          DashboardDocument dashboardDocument = new DashboardDocument();
          dashboardDocument.setId(attachment.getId());
          dashboardDocument.setFilename(attachment.getFilename());
          dashboardDocument.setUrl(url);
          return dashboardDocument;
        }).collect(Collectors.toList());
  }

}
