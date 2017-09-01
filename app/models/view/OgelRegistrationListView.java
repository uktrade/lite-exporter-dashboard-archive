package models.view;

import models.Page;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.route.LicenceRoute;

public class OgelRegistrationListView {

  private final LicenceSortType licenceSortType;
  private final SortDirection sortDirection;
  private final Page<OgelRegistrationItemView> page;

  public OgelRegistrationListView(LicenceSortType licenceSortType, SortDirection sortDirection, Page<OgelRegistrationItemView> page) {
    this.licenceSortType = licenceSortType;
    this.sortDirection = sortDirection;
    this.page = page;
  }

  public LicenceSortType getLicenceSortType() {
    return licenceSortType;
  }

  public SortDirection getSortDirection() {
    return sortDirection;
  }

  public Page<OgelRegistrationItemView> getPage() {
    return page;
  }

  public LicenceRoute getLicenceRoute() {
    return new LicenceRoute(licenceSortType, sortDirection, page.getCurrentPage());
  }

}
