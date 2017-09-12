package models.view.route;

import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;

public class LicenceRoute {

  private LicenceSortType licenceSortType;
  private SortDirection sortDirection;
  private Integer page;

  public LicenceRoute(LicenceSortType licenceSortType, SortDirection sortDirection, Integer page) {
    this.licenceSortType = licenceSortType;
    this.sortDirection = sortDirection;
    this.page = page;
  }

  public LicenceRoute nextSort(LicenceSortType sortType) {
    if (licenceSortType == sortType) {
      nextSortDirection();
    } else {
      licenceSortType = sortType;
      if (licenceSortType == LicenceSortType.DATE) {
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

  public LicenceRoute setPage(int page) {
    this.page = page;
    return this;
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("/licences?tab=");
    stringBuilder.append(LicenceListTab.OGELS);
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
