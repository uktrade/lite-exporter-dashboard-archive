package components.dao;

import models.WithdrawalRejection;

import java.util.List;

public interface WithdrawalRejectionDao {

  List<WithdrawalRejection> getWithdrawalRejections(String appId);

  void insertWithdrawalRejection(WithdrawalRejection withdrawalRejection);

  void deleteWithdrawalRejectionsByAppId(String appId);

  void deleteAllWithdrawalRejections();

}
