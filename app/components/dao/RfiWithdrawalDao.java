package components.dao;

import models.RfiWithdrawal;

import java.util.List;

public interface RfiWithdrawalDao {

  List<RfiWithdrawal> getRfiWithdrawals(List<String> rfiIds);

  void insertRfiWithdrawal(RfiWithdrawal rfiWithdrawal);

  void deleteAllRfiWithdrawals();

  void deleteRfiWithdrawalByRfiId(String rfiId);

}
