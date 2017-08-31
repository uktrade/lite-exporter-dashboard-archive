package components.service;

import com.google.inject.Inject;
import components.dao.WithdrawalRequestDao;
import components.util.RandomUtil;
import models.User;

import java.time.Instant;

public class WithdrawalRequestServiceImpl implements WithdrawalRequestService {

  private final UserService userService;
  private final WithdrawalRequestDao withdrawalRequestDao;

  @Inject
  public WithdrawalRequestServiceImpl(UserService userService, WithdrawalRequestDao withdrawalRequestDao) {
    this.userService = userService;
    this.withdrawalRequestDao = withdrawalRequestDao;
  }

  @Override
  public void insertWithdrawalRequest(String appId, String message) {
    User currentUser = userService.getCurrentUser();
    models.WithdrawalRequest withdrawalRequest = new models.WithdrawalRequest(RandomUtil.random("WIT"),
        appId,
        Instant.now().toEpochMilli(),
        currentUser.getId(),
        message,
        null,
        null,
        null,
        null);
    withdrawalRequestDao.insertWithdrawalRequest(withdrawalRequest);
  }

}
