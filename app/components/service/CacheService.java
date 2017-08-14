package components.service;

import models.ApplicationListState;
import models.LicenseListState;
import scala.Option;

public interface CacheService {

  ApplicationListState getApplicationListState(Option<String> tab, Option<String> date, Option<String> status, Option<String> show, Option<String> company, Option<Integer> page);

  LicenseListState getLicenseListState(Option<String> tab, Option<String> reference, Option<String> licensee, Option<String> site, Option<String> date, Option<Integer> page);

}
