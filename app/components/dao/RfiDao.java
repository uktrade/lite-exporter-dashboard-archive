package components.dao;

import models.Rfi;

import java.util.List;

public interface RfiDao {

  Rfi getRfi(String rfiId);

  List<Rfi> getRfiList(List<String> appIds);

  void insertRfi(Rfi rfi);

  void deleteAllRfiData();

  void deleteRfiListByAppId(String appId);

}
