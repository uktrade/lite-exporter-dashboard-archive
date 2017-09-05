package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.message.MessagePublisher;
import components.util.RandomUtil;
import models.Amendment;
import models.User;
import models.enums.RoutingKey;

import java.time.Instant;

public class AmendmentServiceImpl implements AmendmentService {

  private final UserService userService;
  private final AmendmentDao amendmentDao;
  private final MessagePublisher messagePublisher;

  @Inject
  public AmendmentServiceImpl(UserService userService, AmendmentDao amendmentDao, MessagePublisher messagePublisher) {
    this.userService = userService;
    this.amendmentDao = amendmentDao;
    this.messagePublisher = messagePublisher;
  }

  @Override
  public void insertAmendment(String appId, String message) {
    User currentUser = userService.getCurrentUser();
    Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), currentUser.getId(), message, null);
    amendmentDao.insertAmendment(amendment);
    messagePublisher.sendMessage(RoutingKey.AMEND_CREATE, amendment);
  }

}
