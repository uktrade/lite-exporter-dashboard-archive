package components.dao;

import java.util.List;
import models.Read;

public interface ReadDao {

  List<Read> getReadList(String userId);

  void insertRead(Read read);

  void deleteAllReadDataByUserId(String userId);

  void deleteAllReadData();

}
