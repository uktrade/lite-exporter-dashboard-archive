package controllers;

import com.google.inject.Inject;
import components.dao.AmendmentRequestDao;
import components.dao.DraftFileDao;
import components.dao.WithdrawalRequestDao;
import components.service.AppDataService;
import components.service.UserPermissionService;
import components.service.UserService;
import components.util.ApplicationUtil;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.AmendmentRequest;
import models.AppData;
import models.File;
import models.RfiReply;
import models.WithdrawalRequest;
import models.enums.DraftType;
import org.apache.commons.collections4.ListUtils;
import play.mvc.Result;
import play.mvc.With;

@With(AppGuardAction.class)
public class DownloadController extends SamlController {

  private final UserService userService;
  private final AppDataService appDataService;
  private final DraftFileDao draftFileDao;
  private final AmendmentRequestDao amendmentRequestDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final UserPermissionService userPermissionService;

  @Inject
  public DownloadController(UserService userService,
                            AppDataService appDataService,
                            DraftFileDao draftFileDao,
                            AmendmentRequestDao amendmentRequestDao,
                            WithdrawalRequestDao withdrawalRequestDao,
                            UserPermissionService userPermissionService) {
    this.userService = userService;
    this.appDataService = appDataService;
    this.draftFileDao = draftFileDao;
    this.amendmentRequestDao = amendmentRequestDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.userPermissionService = userPermissionService;
  }

  public Result getRfiReplyFile(String appId, String rfiId, String fileId) {
    String userId = userService.getCurrentUserId();
    AppData appData = appDataService.getAppData(appId);
    Optional<RfiReply> rfiReply = ApplicationUtil.getAllRfiReplies(appData).stream()
        .filter(reply -> reply.getRfiId().equals(rfiId))
        .findAny();
    if (rfiReply.isPresent() && containsFile(rfiReply.get().getAttachments(), fileId)) {
      return getFile(rfiReply.get().getAttachments(), fileId);
    } else {
      List<File> draftFiles = draftFileDao.getDraftFiles(rfiId, DraftType.RFI_REPLY);
      if (containsFile(draftFiles, fileId) && userPermissionService.canAddRfiReply(userId, rfiId, appData)) {
        return getFile(draftFiles, fileId);
      }
    }
    return unknownFile(fileId);
  }

  public Result getAmendmentOrWithdrawalFile(String appId, String fileId) {
    List<File> amendmentFiles = amendmentRequestDao.getAmendmentRequests(appId).stream()
        .map(AmendmentRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> withdrawalFiles = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId).stream()
        .map(WithdrawalRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> amendmentOrWithdrawalFiles = ListUtils.union(amendmentFiles, withdrawalFiles);
    if (containsFile(amendmentOrWithdrawalFiles, fileId)) {
      return getFile(amendmentOrWithdrawalFiles, fileId);
    } else {
      String userId = userService.getCurrentUserId();
      AppData appData = appDataService.getAppData(appId);
      List<File> draftFiles = draftFileDao.getDraftFiles(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      if (containsFile(draftFiles, fileId) && userPermissionService.canAddAmendmentOrWithdrawalRequest(userId, appData)) {
        return getFile(draftFiles, fileId);
      }
    }
    return unknownFile(fileId);
  }

  private boolean containsFile(List<File> files, String fileId) {
    return files.stream()
        .anyMatch(file -> file.getId().equals(fileId));
  }

  private Result getFile(List<File> files, String fileId) {
    Optional<File> file = files.stream()
        .filter(f -> f.getId().equals(fileId))
        .findAny();
    if (file.isPresent()) {
      return ok(new java.io.File(file.get().getUrl()));
    } else {
      return unknownFile(fileId);
    }
  }

  private Result unknownFile(String fileId) {
    return notFound("No file found with fileId " + fileId);
  }

}
