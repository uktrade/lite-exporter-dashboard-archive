package components.service;

import models.view.OgelItemView;

import java.util.List;

public interface TestDataService {

  void deleteAllDataAndInsertTwoCompaniesTestData();

  void deleteAllDataAndInsertOneCompanyTestData();

  void deleteAllData();

  void deleteAllDataAndInsertOtherUserApplications();

  List<OgelItemView> recycleOgelItemView(OgelItemView base);

}
