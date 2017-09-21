package components.service;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.util.FileUtil;
import components.util.TimeUtil;
import models.Rfi;
import models.enums.DraftType;
import models.view.AddRfiReplyView;
import models.view.FileView;
import models.view.RfiReplyView;
import models.view.RfiView;
import uk.gov.bis.lite.exporterdashboard.api.File;
import uk.gov.bis.lite.exporterdashboard.api.RfiReply;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RfiViewServiceImpl implements RfiViewService {

  private final RfiDao rfiDao;
  private final RfiReplyDao rfiReplyDao;
  private final UserService userService;
  private final DraftDao draftDao;

  @Inject
  public RfiViewServiceImpl(RfiDao rfiDao,
                            RfiReplyDao rfiReplyDao,
                            UserService userService,
                            DraftDao draftDao) {
    this.rfiDao = rfiDao;
    this.rfiReplyDao = rfiReplyDao;
    this.userService = userService;
    this.draftDao = draftDao;
  }

  @Override
  public List<RfiView> getRfiViews(String appId) {
    List<Rfi> rfiList = rfiDao.getRfiList(appId);
    return rfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(this::getRfiView)
        .collect(Collectors.toList());
  }

  @Override
  public int getRfiViewCount(String appId) {
    return rfiDao.getRfiCount(appId);
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

  private RfiView getRfiView(Rfi rfi) {
    String receivedOn = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUsername(rfi.getSentBy());
    RfiReplyView rfiReplyView = getRfiReplyView(rfi.getAppId(), rfi.getRfiId());
    return new RfiView(rfi.getAppId(), rfi.getRfiId(), receivedOn, replyBy, sender, rfi.getMessage(), rfiReplyView);
  }

  private RfiReplyView getRfiReplyView(String appId, String rfiId) {
    RfiReply rfiReply = rfiReplyDao.getRfiReply(rfiId);
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
