package components.service;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import components.message.SpireRelayPublisher;
import models.RfiResponse;
import models.enums.RoutingKey;

import java.time.Instant;

public class RfiResponseServiceImpl implements RfiResponseService {

  private final UserService userService;
  private final RfiResponseDao rfiResponseDao;
  private final SpireRelayPublisher spireRelayPublisher;

  @Inject
  public RfiResponseServiceImpl(UserService userService, RfiResponseDao rfiResponseDao, SpireRelayPublisher spireRelayPublisher) {
    this.userService = userService;
    this.rfiResponseDao = rfiResponseDao;
    this.spireRelayPublisher = spireRelayPublisher;
  }

  @Override
  public void insertRfiResponse(String rfiId, String message) {
    RfiResponse rfiResponse = new RfiResponse(rfiId, userService.getCurrentUser().getId(), Instant.now().toEpochMilli(), message, null);
    rfiResponseDao.insertRfiResponse(rfiResponse);
    spireRelayPublisher.sendMessage(RoutingKey.RFI_REPLY, rfiResponse);
  }

}
