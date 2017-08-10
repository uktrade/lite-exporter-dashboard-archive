package components.dao;

import models.Rfi;

import java.util.List;

public interface RfiDao {

  List<Rfi> getRfiList();

  List<Rfi> getRfiList(String appId);

  int getRfiCount(String appId);

  void insertRfi(Rfi rfi);

  void deleteAllRfiData();
}
