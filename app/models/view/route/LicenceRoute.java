package models.view.route;

import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;

public class LicenceRoute extends Route {

  private LicenceListTab licenceListTab;
  private LicenceSortType licenceSortType;
  private SortDirection sortDirection;
  private Integer page;

  public LicenceRoute(LicenceListTab licenceListTab, LicenceSortType licenceSortType, SortDirection sortDirection, Integer page) {
    this.licenceListTab = licenceListTab;
    this.licenceSortType = licenceSortType;
    this.sortDirection = sortDirection;
    this.page = page;
  }

  public LicenceRoute nextSort(LicenceSortType sortType) {
    if (licenceSortType == sortType) {
      nextSortDirection();
    } else {
      licenceSortType = sortType;
      if (licenceSortType == LicenceSortType.REGISTRATION_DATE || licenceSortType == LicenceSortType.EXPIRY_DATE) {
        sortDirection = SortDirection.DESC;
      } else {
        sortDirection = SortDirection.ASC;
      }
    }
    return this;
  }

  private void nextSortDirection() {
    if (sortDirection == SortDirection.DESC) {
      sortDirection = SortDirection.ASC;
    } else {
      sortDirection = SortDirection.DESC;
    }
  }

  public LicenceRoute setLicenceListTab(LicenceListTab licenceListTab) {
    this.licenceListTab = licenceListTab;
    return this;
  }

  public LicenceRoute setPage(int page) {
    this.page = page;
    return this;
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("/licences?tab=");
    stringBuilder.append(licenceListTab);
    if (licenceSortType != null) {
      stringBuilder.append("&sort=");
      stringBuilder.append(licenceSortType);
    }
    if (sortDirection != null) {
      stringBuilder.append("&direction=");
      stringBuilder.append(sortDirection);
    }
    if (page != null && page != 1) {
      stringBuilder.append("&page=");
      stringBuilder.append(page);
    }
    return stringBuilder.toString();
  }

}
