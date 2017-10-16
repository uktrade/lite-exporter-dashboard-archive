package components.dao;

import models.Rfi;

import java.util.List;

public interface RfiDao {

  List<Rfi> getRfiList(List<String> appIds);

  void insertRfi(Rfi rfi);

  void deleteAllRfiData();

  void deleteRfiListByAppId(String appId);

}
