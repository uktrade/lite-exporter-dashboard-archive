package components.service;

import java.util.List;
import models.AppData;

public interface AppDataService {

  List<AppData> getAppDataList(List<String> customerIds);

  AppData getAppData(String appId);

}
