package controllers;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.dao.DraftDao;
import components.dao.RfiReplyDao;
import components.dao.WithdrawalRequestDao;
import components.service.AppDataService;
import components.service.UserPrivilegeService;
import components.service.UserService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Amendment;
import models.AppData;
import models.File;
import models.RfiReply;
import models.WithdrawalRequest;
import models.enums.DraftType;
import org.apache.commons.collections4.ListUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.mvc.Result;
import play.mvc.With;

@With(AppGuardAction.class)
public class DownloadController extends SamlController {

  private static final Logger LOGGER = LoggerFactory.getLogger(DownloadController.class);

  private final UserService userService;
  private final AppDataService appDataService;
  private final RfiReplyDao rfiReplyDao;
  private final DraftDao draftDao;
  private final AmendmentDao amendmentDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final UserPrivilegeService userPrivilegeService;

  @Inject
  public DownloadController(UserService userService,
                            AppDataService appDataService,
                            RfiReplyDao rfiReplyDao,
                            DraftDao draftDao,
                            AmendmentDao amendmentDao,
                            WithdrawalRequestDao withdrawalRequestDao,
                            UserPrivilegeService userPrivilegeService) {
    this.userService = userService;
    this.appDataService = appDataService;
    this.rfiReplyDao = rfiReplyDao;
    this.draftDao = draftDao;
    this.amendmentDao = amendmentDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.userPrivilegeService = userPrivilegeService;
  }

  public Result getRfiReplyFile(String appId, String rfiId, String fileId) {
    LOGGER.error("getRfiReplyFile " + appId + " " + rfiId + " " + fileId);
    RfiReply rfiReply = rfiReplyDao.getRfiReply(rfiId);
    if (rfiReply != null && containsFile(rfiReply.getAttachments(), fileId)) {
      return getFile(rfiReply.getAttachments(), fileId);
    } else {
      String userId = userService.getCurrentUserId();
      AppData appData = appDataService.getAppData(appId);
      List<File> draftFiles = draftDao.getDraftAttachments(rfiId, DraftType.RFI_REPLY);
      if (containsFile(draftFiles, fileId) && userPrivilegeService.isReplyAllowed(userId, rfiId, appData)) {
        return getFile(draftFiles, fileId);
      }
    }
    return unknownFile(fileId);
  }

  public Result getAmendmentOrWithdrawalFile(String appId, String fileId) {
    List<File> amendmentFiles = amendmentDao.getAmendments(appId).stream()
        .map(Amendment::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> withdrawalFiles = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId).stream()
        .map(WithdrawalRequest::getAttachments)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    List<File> amendmentOrWithdrawalFiles = ListUtils.union(amendmentFiles, withdrawalFiles);
    if (containsFile(amendmentOrWithdrawalFiles, fileId)) {
      return getFile(amendmentFiles, fileId);
    } else {
      String userId = userService.getCurrentUserId();
      AppData appData = appDataService.getAppData(appId);
      List<File> draftFiles = draftDao.getDraftAttachments(appId, DraftType.AMENDMENT_OR_WITHDRAWAL);
      if (containsFile(draftFiles, fileId) && userPrivilegeService.isAmendmentOrWithdrawalAllowed(userId, appData)) {
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
