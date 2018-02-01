package controllers;

import com.google.inject.Inject;
import components.common.upload.FileService;
import components.dao.AmendmentRequestDao;
import components.dao.DraftFileDao;
import components.dao.WithdrawalRequestDao;
import components.service.AppDataService;
import components.service.UserPermissionService;
import components.service.UserService;
import components.util.ApplicationUtil;
import models.AmendmentRequest;
import models.AppData;
import models.Attachment;
import models.RfiReply;
import models.WithdrawalRequest;
import models.enums.DraftType;
import org.apache.commons.collections4.ListUtils;
import play.mvc.Result;
import play.mvc.With;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@With(AppGuardAction.class)
public class DownloadController extends SamlController {

  private final UserService userService;
  private final AppDataService appDataService;
  private final DraftFileDao draftFileDao;
  private final AmendmentRequestDao amendmentRequestDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final UserPermissionService userPermissionService;
  private final FileService fileService;

  @Inject
  public DownloadController(UserService userService,
                            AppDataService appDataService,
                            DraftFileDao draftFileDao,
                            AmendmentRequestDao amendmentRequestDao,
                            WithdrawalRequestDao withdrawalRequestDao,
                            UserPermissionService userPermissionService,
                            FileService fileService) {
    this.userService = userService;
    this.appDataService = appDataService;
    this.draftFileDao = draftFileDao;
    this.amendmentRequestDao = amendmentRequestDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.userPermissionService = userPermissionService;
    this.fileService = fileService;
  }

  public Result getRfiReplyAttachment(String appId, String rfiId, String id) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    Optional<RfiReply> rfiReply = ApplicationUtil.getAllRfiReplies(appData).stream()
        .filter(reply -> reply.getRfiId().equals(rfiId))
        .findAny();
    if (rfiReply.isPresent() && containsAttachment(rfiReply.get().getAttachments(), id)) {
      return getAttachment(rfiReply.get().getAttachments(), id);
    } else {
      List<Attachment> draftAttachments = draftFileDao.getAttachments(rfiId, DraftType.RFI_REPLY);
      if (containsAttachment(draftAttachments, id) && userPermissionService.canAddRfiReply(userId, rfiId, appData)) {
        return getAttachment(draftAttachments, id);
      }
    }
    return unknownAttachment(id);
  }

  public Result getAmendmentOrWithdrawalAttachment(String appId, String id) {
    List<Attachment> amendmentAttachments = amendmentRequestDao.getAmendmentRequests(appId).stream()
        .map(AmendmentRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<Attachment> withdrawalAttachments = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId).stream()
        .map(WithdrawalRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<Attachment> amendmentOrWithdrawalAttachments = ListUtils.union(amendmentAttachments, withdrawalAttachments);
    if (containsAttachment(amendmentOrWithdrawalAttachments, id)) {
      return getAttachment(amendmentOrWithdrawalAttachments, id);
    } else {
      String userId = userService.getCurrentUserId();
      AppData appData = appDataService.getAppData(appId);
      List<Attachment> draftAttachments = draftFileDao.getAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      if (containsAttachment(draftAttachments, id) && userPermissionService.canAddAmendmentOrWithdrawalRequest(userId, appData)) {
        return getAttachment(draftAttachments, id);
      }
    }
    return unknownAttachment(id);
  }

  private boolean containsAttachment(List<Attachment> attachments, String id) {
    return attachments.stream()
        .anyMatch(attachment -> attachment.getId().equals(id));
  }

  private Result getAttachment(List<Attachment> attachments, String id) {
    Optional<Attachment> attachmentOptional = attachments.stream()
        .filter(attachment -> attachment.getId().equals(id))
        .findAny();
    if (attachmentOptional.isPresent()) {
      Attachment attachment = attachmentOptional.get();
      return ok(fileService.retrieveFile(attachment.getId(), attachment.getBucket(), attachment.getFolder()));
    } else {
      return unknownAttachment(id);
    }
  }

  private Result unknownAttachment(String id) {
    return notFound("No attachment found with id " + id);
  }

}
