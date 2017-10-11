package components.service;

import java.util.List;
import java.util.Map;
import models.AppData;
import models.ReadData;

public interface ReadDataService {

  Map<String, ReadData> getReadData(String userId, List<AppData> appDataList);

  ReadData getReadData(String userId, AppData appData);

  void updateRfiTabReadData(String userId, AppData appData, ReadData readData);

  void updateMessageTabReadData(String userId, AppData appData, ReadData readData);

  void updateDocumentTabReadData(String userId, AppData appData, ReadData readData);

}
