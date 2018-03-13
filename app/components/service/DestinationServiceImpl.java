package components.service;

import com.google.inject.Inject;
import components.common.cache.CountryProvider;
import models.Application;
import uk.gov.bis.lite.countryservice.api.CountryView;

import java.util.HashSet;
import java.util.Set;

public class DestinationServiceImpl implements DestinationService {

  private final CountryProvider countryProvider;

  @Inject
  public DestinationServiceImpl(CountryProvider countryProvider) {
    this.countryProvider = countryProvider;
  }

  @Override
  public String getDestination(Application application) {
    if (application.getConsigneeCountries().size() != 1) {
      return "";
    } else {
      return getDestination(application.getConsigneeCountries().get(0), new HashSet<>(application.getEndUserCountries()));
    }
  }

  private String getDestination(String consigneeCountry, Set<String> endUserCountries) {
    Set<String> uniqueEndUserCountries = new HashSet<>(endUserCountries);
    uniqueEndUserCountries.remove(consigneeCountry);
    if (uniqueEndUserCountries.isEmpty()) {
      return getCountryName(consigneeCountry);
    } else if (uniqueEndUserCountries.size() == 1) {
      String endUserCountry = uniqueEndUserCountries.iterator().next();
      String endUserCountryName = getCountryName(endUserCountry);
      String consigneeCountryName = getCountryName(consigneeCountry);
      if (consigneeCountryName.compareToIgnoreCase(endUserCountryName) < 0) {
        return consigneeCountryName + "; " + endUserCountryName;
      } else {
        return endUserCountryName + "; " + consigneeCountryName;
      }
    } else {
      return getCountryName(consigneeCountry) + " + " + endUserCountries.size() + " end user destinations";
    }
  }

  private String getCountryName(String countryRef) {
    CountryView countryView = countryProvider.getCountry(countryRef);
    if (countryView == null) {
      return countryRef;
    } else {
      return countryView.getCountryName();
    }
  }

}
