package components.service;

import models.ApplicationListState;
import models.LicenceListState;

public interface CacheService {

  ApplicationListState getApplicationListState(String tab, String date, String status, String show, String company, String createdBy, Integer page);

  LicenceListState getLicenseListState(String tab, String reference, String licensee, String site, String date, Integer page);

}
