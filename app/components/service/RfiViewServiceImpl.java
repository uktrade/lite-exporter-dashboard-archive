package components.service;

import com.google.inject.Inject;
import components.dao.DraftDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.util.FileUtil;
import components.util.TimeUtil;
import models.File;
import models.Rfi;
import models.RfiResponse;
import models.enums.DraftType;
import models.view.AddRfiResponseView;
import models.view.FileView;
import models.view.RfiResponseView;
import models.view.RfiView;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RfiViewServiceImpl implements RfiViewService {

  private final RfiDao rfiDao;
  private final RfiResponseDao rfiResponseDao;
  private final UserService userService;
  private final DraftDao draftDao;

  @Inject
  public RfiViewServiceImpl(RfiDao rfiDao,
                            RfiResponseDao rfiResponseDao,
                            UserService userService,
                            DraftDao draftDao) {
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
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
  public AddRfiResponseView getAddRfiResponseView(String appId, String rfiId) {
    List<File> draftAttachments = draftDao.getDraftAttachments(rfiId, DraftType.RFI_RESPONSE);
    List<FileView> fileViews = createFileViews(appId, rfiId, draftAttachments);
    return new AddRfiResponseView(TimeUtil.formatDate(Instant.now().toEpochMilli()), rfiId, fileViews);
  }

  private List<FileView> createFileViews(String appId, String rfiId, List<File> files) {
    return files.stream()
        .map(file -> createFileView(appId, rfiId, file))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String appId, String rfiId, File file) {
    String link = controllers.routes.DownloadController.getRfiFile(rfiId, file.getFileId()).toString();
    String deleteLink = controllers.routes.RfiTabController.deleteFileById(appId, file.getFileId()).toString();
    return new FileView(file.getFileId(), rfiId, file.getName(), link, deleteLink, FileUtil.getReadableFileSize(file.getPath()));
  }

  private RfiView getRfiView(Rfi rfi) {
    String receivedOn = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUsername(rfi.getSentBy());
    RfiResponseView rfiResponseView = getRfiResponseView(rfi.getAppId(), rfi.getRfiId());
    return new RfiView(rfi.getAppId(), rfi.getRfiId(), receivedOn, replyBy, sender, rfi.getMessage(), rfiResponseView);
  }

  private RfiResponseView getRfiResponseView(String appId, String rfiId) {
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    if (rfiResponse != null) {
      String sentBy = userService.getUsername(rfiResponse.getSentBy());
      String sentAt = TimeUtil.formatDate(rfiResponse.getSentTimestamp());
      String message = rfiResponse.getMessage();
      List<FileView> fileViews = createFileViews(appId, rfiId, rfiResponse.getAttachments());
      return new RfiResponseView(sentBy, sentAt, message, fileViews);
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
