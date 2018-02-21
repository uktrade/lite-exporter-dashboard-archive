package components.service;

import com.google.inject.Inject;
import components.common.upload.FileUtil;
import components.common.upload.FileView;
import components.dao.DraftFileDao;
import components.util.ApplicationUtil;
import components.util.Comparators;
import components.util.TimeUtil;
import models.AppData;
import models.Attachment;
import models.Rfi;
import models.RfiReply;
import models.RfiWithdrawal;
import models.enums.DraftType;
import models.view.AddRfiReplyView;
import models.view.RfiReplyView;
import models.view.RfiView;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RfiViewServiceImpl implements RfiViewService {

  private final UserService userService;
  private final DraftFileDao draftFileDao;
  private final UserPermissionService userPermissionService;

  @Inject
  public RfiViewServiceImpl(UserService userService, DraftFileDao draftFileDao, UserPermissionService userPermissionService) {
    this.userService = userService;
    this.draftFileDao = draftFileDao;
    this.userPermissionService = userPermissionService;
  }

  @Override
  public List<RfiView> getRfiViews(String userId, AppData appData) {

    List<Rfi> rfiList = ApplicationUtil.getAllRfi(appData);

    Map<String, RfiWithdrawal> rfiIdToRfiWithdrawal = ApplicationUtil.getAllRfiWithdrawals(appData).stream()
        .collect(Collectors.toMap(RfiWithdrawal::getRfiId, Function.identity()));

    Map<String, RfiReply> rfiIdToRfiReply = ApplicationUtil.getAllRfiReplies(appData).stream()
        .collect(Collectors.toMap(RfiReply::getRfiId, Function.identity()));

    Map<String, Boolean> rfiIdToIsReplyAllowed = rfiList.stream()
        .collect(Collectors.toMap(Rfi::getId, rfi -> userPermissionService.canAddRfiReply(userId, rfi.getId(), appData)));

    return rfiList.stream()
        .sorted(Comparators.RFI_CREATED_REVERSED)
        .map(rfi -> {
          String rfiId = rfi.getId();
          return getRfiView(appData.getApplication().getId(),
              rfi,
              rfiIdToRfiReply.get(rfiId),
              rfiIdToRfiWithdrawal.get(rfiId),
              rfiIdToIsReplyAllowed.get(rfiId));
        })
        .collect(Collectors.toList());
  }

  @Override
  public AddRfiReplyView getAddRfiReplyView(String appId, String rfiId) {
    List<Attachment> draftAttachments = draftFileDao.getAttachments(rfiId, DraftType.RFI_REPLY);
    List<FileView> fileViews = createFileViews(appId, rfiId, draftAttachments);
    return new AddRfiReplyView(rfiId, fileViews);
  }

  private List<FileView> createFileViews(String appId, String rfiId, List<Attachment> attachments) {
    return attachments.stream()
        .map(attachment -> createFileView(appId, rfiId, attachment))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, String rfiId, Attachment attachment) {
    String link = controllers.routes.DownloadController.getRfiReplyAttachment(appId, rfiId, attachment.getId()).toString();
    String jsDeleteLink = controllers.routes.UploadController.deleteFile(appId, attachment.getId()).toString();
    String size = FileUtil.getReadableFileSize(attachment.getSize());
    return new FileView(attachment.getId(), attachment.getFilename(), link, size, jsDeleteLink);
  }

  private RfiView getRfiView(String appId, Rfi rfi, RfiReply rfiReply, RfiWithdrawal rfiWithdrawal, boolean isReplyAllowed) {
    boolean showNewIndicator = rfiWithdrawal == null && rfiReply == null;
    String withdrawnDate;
    if (rfiWithdrawal != null) {
      withdrawnDate = TimeUtil.formatDate(rfiWithdrawal.getCreatedTimestamp());
    } else {
      withdrawnDate = null;
    }
    String receivedDate = TimeUtil.formatDateAndTime(rfi.getCreatedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUsername(rfi.getCreatedByUserId());
    RfiReplyView rfiReplyView = getRfiReplyView(appId, rfi.getId(), rfiReply);
    return new RfiView(appId, rfi.getId(), receivedDate, replyBy, sender, rfi.getMessage(), withdrawnDate, showNewIndicator, rfiReplyView, isReplyAllowed);
  }

  private RfiReplyView getRfiReplyView(String appId, String rfiId, RfiReply rfiReply) {
    if (rfiReply != null) {
      String sentBy = userService.getUsername(rfiReply.getCreatedByUserId());
      String sentAt = TimeUtil.formatDateAndTime(rfiReply.getCreatedTimestamp());
      String message = rfiReply.getMessage();
      List<FileView> fileViews = createFileViews(appId, rfiId, rfiReply.getAttachments());
      return new RfiReplyView(sentBy, sentAt, message, fileViews);
    } else {
      return null;
    }
  }

  private String getReplyBy(Rfi rfi) {
    if (rfi.getDueTimestamp() != null) {
      Long daysRemaining = TimeUtil.daysBetweenWithStartBeforeEnd(Instant.now().toEpochMilli(), rfi.getDueTimestamp());
      String dueBy = TimeUtil.formatDate(rfi.getDueTimestamp());
      if (daysRemaining >= 0) {
        return String.format("%s (%d days remaining)", dueBy, daysRemaining);
      } else {
        return String.format("%s (%d days overdue)", dueBy, -daysRemaining);
      }
    } else {
      return "";
    }
  }

}
