package components.service;

import com.google.inject.Inject;
import components.dao.AmendmentDao;
import components.util.RandomUtil;
import models.Amendment;
import models.User;

import java.time.Instant;

public class AmendmentServiceImpl implements AmendmentService {

  private final UserService userService;
  private final AmendmentDao amendmentDao;

  @Inject
  public AmendmentServiceImpl(UserService userService, AmendmentDao amendmentDao) {
    this.userService = userService;
    this.amendmentDao = amendmentDao;
  }

  @Override
  public void insertAmendment(String appId, String message) {
    User currentUser = userService.getCurrentUser();
    Amendment amendment = new Amendment(RandomUtil.random("AME"), appId, Instant.now().toEpochMilli(), currentUser.getId(), message, null);
    amendmentDao.insertAmendment(amendment);
  }

}
