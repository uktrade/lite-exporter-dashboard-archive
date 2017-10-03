package components.service;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import components.exceptions.UnexpectedStateException;
import components.util.SortUtil;
import models.WithdrawalApproval;
import models.WithdrawalInformation;
import models.WithdrawalRejection;
import uk.gov.bis.lite.exporterdashboard.api.WithdrawalRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WithdrawalServiceImpl implements WithdrawalService {

  private final WithdrawalRejectionDao withdrawalRejectionDao;
  private final WithdrawalRequestDao withdrawalRequestDao;
  private final WithdrawalApprovalDao withdrawalApprovalDao;

  @Inject
  public WithdrawalServiceImpl(WithdrawalRejectionDao withdrawalRejectionDao, WithdrawalRequestDao withdrawalRequestDao, WithdrawalApprovalDao withdrawalApprovalDao) {
    this.withdrawalRejectionDao = withdrawalRejectionDao;
    this.withdrawalRequestDao = withdrawalRequestDao;
    this.withdrawalApprovalDao = withdrawalApprovalDao;
  }

  @Override
  public Map<String, WithdrawalInformation> getAppIdToWithdrawalInformationMap(List<String> appIds) {
    List<WithdrawalApproval> withdrawalApprovals = withdrawalApprovalDao.getWithdrawalApprovals(appIds);
    Map<String, WithdrawalApproval> withdrawalApprovalMap = new HashMap<>();
    withdrawalApprovals.forEach(withdrawalApproval -> withdrawalApprovalMap.put(withdrawalApproval.getAppId(), withdrawalApproval));

    List<WithdrawalRequest> withdrawalRequests = withdrawalRequestDao.getWithdrawalRequestsByAppIds(appIds);
    Multimap<String, WithdrawalRequest> withdrawalRequestMultimap = HashMultimap.create();
    withdrawalRequests.forEach(withdrawalRequest -> withdrawalRequestMultimap.put(withdrawalRequest.getAppId(), withdrawalRequest));

    List<WithdrawalRejection> withdrawalRejections = withdrawalRejectionDao.getWithdrawalRejectionsByAppIds(appIds);
    Multimap<String, WithdrawalRejection> withdrawalRejectionMultimap = HashMultimap.create();
    withdrawalRejections.forEach(withdrawalRejection -> withdrawalRejectionMultimap.put(withdrawalRejection.getAppId(), withdrawalRejection));

    return appIds.stream()
        .map(appId -> getWithdrawalInformation(appId,
            withdrawalApprovalMap.get(appId),
            new ArrayList<>(withdrawalRequestMultimap.get(appId)),
            new ArrayList<>(withdrawalRejectionMultimap.get(appId))))
        .collect(Collectors.toMap(WithdrawalInformation::getAppId, Function.identity()));
  }

  @Override
  public WithdrawalInformation getWithdrawalInformation(String appId) {
    WithdrawalApproval withdrawalApproval = withdrawalApprovalDao.getWithdrawalApproval(appId);
    List<WithdrawalRequest> withdrawalRequests = withdrawalRequestDao.getWithdrawalRequestsByAppId(appId);
    List<WithdrawalRejection> withdrawalRejections = withdrawalRejectionDao.getWithdrawalRejectionsByAppId(appId);
    return getWithdrawalInformation(appId, withdrawalApproval, withdrawalRequests, withdrawalRejections);
  }

  private WithdrawalInformation getWithdrawalInformation(String appId, WithdrawalApproval withdrawalApproval, List<WithdrawalRequest> withdrawalRequests, List<WithdrawalRejection> withdrawalRejections) {
    if ((withdrawalApproval == null && withdrawalRejections.size() > withdrawalRequests.size()) ||
        (withdrawalApproval != null && withdrawalRejections.size() + 1 > withdrawalRequests.size())) {
      throw new UnexpectedStateException("There are more withdrawal responses than requests for appId " + appId);
    }

    SortUtil.sortWithdrawalRequests(withdrawalRequests);
    SortUtil.sortWithdrawalRejections(withdrawalRejections);

    withdrawalRejections.forEach(withdrawalRejection -> withdrawalRequests.remove(0));

    WithdrawalRequest approvedWithdrawalRequest;
    if (withdrawalApproval != null) {
      approvedWithdrawalRequest = withdrawalRequests.remove(withdrawalRequests.size() - 1);
    } else {
      approvedWithdrawalRequest = null;
    }
    return new WithdrawalInformation(appId, withdrawalRequests, withdrawalRejections, approvedWithdrawalRequest, withdrawalApproval);
  }

}
