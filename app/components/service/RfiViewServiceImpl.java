package components.service;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.util.Comparators;
import components.util.FileUtil;
import components.util.TimeUtil;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import models.AppData;
import models.Rfi;
import models.RfiWithdrawal;
import models.enums.DraftType;
import models.view.AddRfiReplyView;
import models.view.FileView;
import models.view.RfiReplyView;
import models.view.RfiView;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

public class RfiViewServiceImpl implements RfiViewService {

  private final UserService userService;
  private final DraftDao draftDao;

  @Inject
  public RfiViewServiceImpl(UserService userService,
                            DraftDao draftDao) {
    this.userService = userService;
    this.draftDao = draftDao;
  }

  @Override
  public List<RfiView> getRfiViews(AppData appData) {
    List<Rfi> rfiList = appData.getRfiList();

    Map<String, RfiWithdrawal> rfiIdToRfiWithdrawal = appData.getRfiWithdrawals().stream()
        .collect(Collectors.toMap(RfiWithdrawal::getRfiId, Function.identity()));

    Map<String, RfiReply> rfiIdToRfiReply = appData.getRfiReplies().stream()
        .collect(Collectors.toMap(RfiReply::getRfiId, Function.identity()));

    return rfiList.stream()
        .sorted(Comparators.RFI_RECEIVED_REVERSED)
        .map(rfi -> getRfiView(rfi, rfiIdToRfiReply.get(rfi.getId()), rfiIdToRfiWithdrawal.get(rfi.getId())))
        .collect(Collectors.toList());
  }

  @Override
  public AddRfiReplyView getAddRfiReplyView(String appId, String rfiId) {
    List<File> draftAttachments = draftDao.getDraftAttachments(rfiId, DraftType.RFI_REPLY);
    List<FileView> fileViews = createFileViews(appId, rfiId, draftAttachments);
    return new AddRfiReplyView(rfiId, fileViews);
  }

  private List<FileView> createFileViews(String appId, String rfiId, List<File> files) {
    return files.stream()
        .map(file -> createFileView(appId, rfiId, file))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, String rfiId, File file) {
    String link = controllers.routes.DownloadController.getRfiFile(rfiId, file.getId()).toString();
    String deleteLink = controllers.routes.RfiTabController.deleteFileById(appId, file.getId()).toString();
    return new FileView(file.getId(), rfiId, file.getFilename(), link, deleteLink, FileUtil.getReadableFileSize(file.getUrl()));
  }

  private RfiView getRfiView(Rfi rfi, RfiReply rfiReply, RfiWithdrawal rfiWithdrawal) {
    boolean showNewIndicator = rfiWithdrawal == null && rfiReply == null;
    String withdrawnDate;
    if (rfiWithdrawal != null) {
      withdrawnDate = TimeUtil.formatDate(rfiWithdrawal.getCreatedTimestamp());
    } else {
      withdrawnDate = null;
    }
    String receivedDate = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUsername(rfi.getSentBy());
    RfiReplyView rfiReplyView = getRfiReplyView(rfi.getAppId(), rfi.getId(), rfiReply);
    return new RfiView(rfi.getAppId(), rfi.getId(), receivedDate, replyBy, sender, rfi.getMessage(), withdrawnDate, showNewIndicator, rfiReplyView);
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
