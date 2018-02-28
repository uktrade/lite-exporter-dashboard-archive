package components.service;

import models.AppData;

import java.util.List;

public interface AppDataService {

  List<AppData> getAppDataList(List<String> customerIds, String userId);

  AppData getAppData(String appId);

}
