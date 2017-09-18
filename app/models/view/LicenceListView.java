package models.view;

import models.Page;
import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.route.LicenceRoute;

public class LicenceListView implements ListView {

  private final LicenceListTab licenceListTab;
  private final LicenceSortType licenceSortType;
  private final SortDirection sortDirection;
  private final Page<OgelItemView> ogelPage;
  private final Page<SielItemView> sielPage;
  private final int currentPage;

  public LicenceListView(LicenceListTab licenceListTab, LicenceSortType licenceSortType, SortDirection sortDirection, Page<OgelItemView> ogelPage, Page<SielItemView> sielPage, int currentPage) {
    this.licenceListTab = licenceListTab;
    this.licenceSortType = licenceSortType;
    this.sortDirection = sortDirection;
    this.ogelPage = ogelPage;
    this.sielPage = sielPage;
    this.currentPage = currentPage;
  }

  public LicenceSortType getLicenceSortType() {
    return licenceSortType;
  }

  public SortDirection getSortDirection() {
    return sortDirection;
  }

  public Page<OgelItemView> getOgelPage() {
    return ogelPage;
  }

  public Page<SielItemView> getSielPage() {
    return sielPage;
  }

  public LicenceListTab getLicenceListTab() {
    return licenceListTab;
  }

  public LicenceRoute getRoute() {
    return new LicenceRoute(licenceListTab, licenceSortType, sortDirection, currentPage);
  }

}
