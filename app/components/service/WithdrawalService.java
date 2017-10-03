package components.service;

import models.WithdrawalInformation;

import java.util.List;
import java.util.Map;

public interface WithdrawalService {

  Map<String, WithdrawalInformation> getAppIdToWithdrawalInformationMap(List<String> appIds);

  WithdrawalInformation getWithdrawalInformation(String appId);

}
