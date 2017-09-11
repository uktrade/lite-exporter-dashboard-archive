package components.service;

import com.google.inject.Inject;
import components.dao.DraftRfiResponseDao;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import components.util.FileUtil;
import components.util.TimeUtil;
import models.DraftRfiResponse;
import models.File;
import models.Rfi;
import models.RfiResponse;
import models.view.AddRfiResponseView;
import models.view.FileView;
import models.view.RfiResponseView;
import models.view.RfiView;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RfiViewServiceImpl implements RfiViewService {

  private final RfiDao rfiDao;
  private final RfiResponseDao rfiResponseDao;
  private final UserService userService;
  private final DraftRfiResponseDao draftRfiResponseDao;

  @Inject
  public RfiViewServiceImpl(RfiDao rfiDao,
                            RfiResponseDao rfiResponseDao,
                            UserService userService,
                            DraftRfiResponseDao draftRfiResponseDao) {
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.userService = userService;
    this.draftRfiResponseDao = draftRfiResponseDao;
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
  public AddRfiResponseView getAddRfiResponseView(String rfiId) {
    DraftRfiResponse draftRfiResponse = draftRfiResponseDao.getDraftRfiResponse(rfiId);
    List<FileView> fileViews;
    if (draftRfiResponse != null) {
      fileViews = createFileViews(rfiId, draftRfiResponse.getAttachments());
    } else {
      fileViews = new ArrayList<>();
    }
    return new AddRfiResponseView(TimeUtil.formatDate(Instant.now().toEpochMilli()), rfiId, fileViews);
  }

  private List<FileView> createFileViews(String rfiId, List<File> files) {
    return files.stream()
        .map(file -> createFileView(rfiId, file))
        .collect(Collectors.toList());
  }

  private FileView createFileView(String rfiId, File file) {
    String link = controllers.routes.RfiTabController.getFile(rfiId, file.getFileId()).toString();
    return new FileView(file.getFileId(), file.getName(), link, FileUtil.getReadableFileSize(file.getPath()));
  }

  private RfiView getRfiView(Rfi rfi) {
    String receivedOn = TimeUtil.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUser(rfi.getSentBy()).getName();
    RfiResponseView rfiResponseView = getRfiResponseView(rfi.getRfiId());
    return new RfiView(rfi.getAppId(), rfi.getRfiId(), receivedOn, replyBy, sender, rfi.getMessage(), rfiResponseView);
  }

  private RfiResponseView getRfiResponseView(String rfiId) {
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    if (rfiResponse != null) {
      String sentBy = userService.getUser(rfiResponse.getSentBy()).getName();
      String sentAt = TimeUtil.formatDate(rfiResponse.getSentTimestamp());
      String message = rfiResponse.getMessage();
      List<FileView> fileViews = createFileViews(rfiId, rfiResponse.getAttachments());
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
