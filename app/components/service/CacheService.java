package components.service;

import models.ApplicationListState;
import models.LicenceListState;

public interface CacheService {

  ApplicationListState getApplicationListState(String tab, String sort, String direction, String show, String company, Integer page);

  LicenceListState getLicenseListState(String tab, String sort, String direction, Integer page);

}
