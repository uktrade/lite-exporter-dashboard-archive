package components.service;

import com.google.inject.Inject;
import components.dao.RfiDao;
import components.dao.RfiResponseDao;
import models.Rfi;
import models.RfiResponse;
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
  private final PersonService personService;

  @Inject
  public RfiViewServiceImpl(TimeFormatService timeFormatService,
                            WorkingDaysCalculatorService workingDaysCalculatorService,
                            RfiDao rfiDao,
                            RfiResponseDao rfiResponseDao,
                            PersonService personService) {
    this.timeFormatService = timeFormatService;
    this.workingDaysCalculatorService = workingDaysCalculatorService;
    this.rfiDao = rfiDao;
    this.rfiResponseDao = rfiResponseDao;
    this.personService = personService;
  }

  @Override
  public List<RfiView> getRfiViewsWithReply(String appId, String rfiId) {
    List<RfiView> rfiViews = getRfiViews(appId);
    rfiViews.stream()
        .filter(rfiView -> rfiView.getRfiId().equals(rfiId))
        .findFirst()
        .ifPresent(rfiView -> {
          String sentAt = timeFormatService.formatDate(Instant.now().toEpochMilli());
          RfiResponseView rfiResponseView = new RfiResponseView("", sentAt, "", true);
          rfiView.getRfiResponseViews().add(0, rfiResponseView);
        });
    return rfiViews;
  }

  @Override
  public List<RfiView> getRfiViews(String appId) {
    List<Rfi> rfiList = rfiDao.getRfiList(appId);
    return rfiList.stream()
        .sorted(Comparator.comparing(Rfi::getReceivedTimestamp))
        .map(rfi -> getRfiView(rfi, rfiResponseDao.getRfiResponses(rfi.getRfiId())))
        .collect(Collectors.toList());
  }

  @Override
  public int getRfiViewCount(String appId) {
    return rfiDao.getRfiCount(appId);
  }

  private RfiView getRfiView(Rfi rfi, List<RfiResponse> rfiResponses) {
    String receivedOn = timeFormatService.formatDateAndTime(rfi.getReceivedTimestamp());
    String replyBy = getReplyBy(rfi);
    String sender = personService.getPerson(rfi.getSentBy());
    List<RfiResponseView> rfiResponseViews = rfiResponses.stream()
        .sorted(Comparator.comparing(RfiResponse::getSentTimestamp))
        .map(this::getRfiResponseView)
        .collect(Collectors.toList());
    return new RfiView(rfi.getAppId(), rfi.getRfiId(), receivedOn, replyBy, sender, rfi.getMessage(), rfiResponseViews);
  }

  private RfiResponseView getRfiResponseView(RfiResponse rfiResponse) {
    String sentBy = personService.getPerson(rfiResponse.getSentBy());
    String sentAt = timeFormatService.formatDate(rfiResponse.getSentTimestamp());
    String message = rfiResponse.getMessage();
    return new RfiResponseView(sentBy, sentAt, message, false);
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
