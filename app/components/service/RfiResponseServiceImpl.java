package components.service;

import com.google.inject.Inject;
import components.dao.RfiResponseDao;
import models.RfiResponse;

import java.time.Instant;

public class RfiResponseServiceImpl implements RfiResponseService {

  private final UserService userService;
  private final RfiResponseDao rfiResponseDao;

  @Inject
  public RfiResponseServiceImpl(UserService userService, RfiResponseDao rfiResponseDao) {
    this.userService = userService;
    this.rfiResponseDao = rfiResponseDao;
  }

  @Override
  public void insertRfiResponse(String rfiId, String message) {
    RfiResponse rfiResponse = new RfiResponse(rfiId, userService.getCurrentUser().getId(), Instant.now().toEpochMilli(), message, null);
    rfiResponseDao.insertRfiResponse(rfiResponse);
  }

}
