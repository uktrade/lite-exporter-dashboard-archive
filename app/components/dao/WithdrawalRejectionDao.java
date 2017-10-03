package components.dao;

import models.WithdrawalRejection;

import java.util.List;

public interface WithdrawalRejectionDao {

  List<WithdrawalRejection> getWithdrawalRejectionsByAppId(String appId);

  List<WithdrawalRejection> getWithdrawalRejectionsByAppIds(List<String> appIds);

  void insertWithdrawalRejection(WithdrawalRejection withdrawalRejection);

  void deleteWithdrawalRejectionsByAppId(String appId);

  void deleteAllWithdrawalRejections();

}
