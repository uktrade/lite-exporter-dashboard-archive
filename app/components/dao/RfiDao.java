package components.dao;

import java.util.List;
import models.Rfi;

public interface RfiDao {

  List<Rfi> getRfiList(List<String> appIds);

  void insertRfi(Rfi rfi);

  void deleteAllRfiData();

  void deleteRfiListByAppId(String appId);

}
