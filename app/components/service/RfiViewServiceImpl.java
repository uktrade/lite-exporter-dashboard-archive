package components.service;

import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import models.Rfi;
import models.RfiResponse;
import models.view.AddRfiResponseView;
import models.view.RfiResponseView;
import models.view.RfiView;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RfiViewServiceImpl implements RfiViewService {

  private final TimeFormatService timeFormatService;
  private final WorkingDaysCalculatorService workingDaysCalculatorService;
  private final RfiDao rfiDao;
  private final RfiResponseDao rfiResponseDao;
  private final UserService userService;

  @Inject
  public RfiViewServiceImpl(TimeFormatService timeFormatService,
                            WorkingDaysCalculatorService workingDaysCalculatorService,
                            RfiDao rfiDao,
                            RfiResponseDao rfiResponseDao,
                            UserService userService) {
    this.timeFormatService = timeFormatService;
    this.workingDaysCalculatorService = workingDaysCalculatorService;
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.userService = userService;
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
    return new AddRfiResponseView(timeFormatService.formatDate(Instant.now().toEpochMilli()), rfiId);
  }

  private RfiView getRfiView(Rfi rfi) {
    String receivedOn = timeFormatService.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = userService.getUser(rfi.getSentBy()).getName();
    RfiResponseView rfiResponseView = getRfiResponseView(rfi.getRfiId());
    return new RfiView(rfi.getAppId(), rfi.getRfiId(), receivedOn, replyBy, sender, rfi.getMessage(), rfiResponseView);
  }

  private RfiResponseView getRfiResponseView(String rfiId) {
    RfiResponse rfiResponse = rfiResponseDao.getRfiResponse(rfiId);
    if (rfiResponse != null) {
      String sentBy = userService.getUser(rfiResponse.getSentBy()).getName();
      String sentAt = timeFormatService.formatDate(rfiResponse.getSentTimestamp());
      String message = rfiResponse.getMessage();
      return new RfiResponseView(sentBy, sentAt, message);
    } else {
      return null;
    }
  }

  private String getReplyBy(Rfi rfi) {
    if (rfi.getDueTimestamp() != null) {
      Long daysRemaining = workingDaysCalculatorService.calculateWithStartBeforeEnd(Instant.now().toEpochMilli(), rfi.getDueTimestamp());
      String dueBy = timeFormatService.formatDate(rfi.getDueTimestamp());
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
