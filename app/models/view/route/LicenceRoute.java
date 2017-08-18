package models.view.route;

import models.enums.LicenceListTab;
import models.enums.SortDirection;

public class LicenceRoute {

  private SortDirection reference;
  private SortDirection licensee;
  private SortDirection site;
  private SortDirection registrationDate;
  private Integer page;

  public LicenceRoute(SortDirection reference, SortDirection licensee, SortDirection site, SortDirection registrationDate, Integer page) {
    this.reference = reference;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
    this.page = page;
  }

  private SortDirection next(SortDirection sortDirection) {
    if (sortDirection == SortDirection.DESC) {
      return SortDirection.ASC;
    } else {
      return SortDirection.DESC;
    }
  }

  private void clearSortDirections() {
    reference = null;
    licensee = null;
    site = null;
    registrationDate = null;
  }

  public LicenceRoute nextReference() {
    SortDirection next = next(reference);
    clearSortDirections();
    this.reference = next;
    return this;
  }

  public LicenceRoute nextLicensee() {
    SortDirection next = next(licensee);
    clearSortDirections();
    this.licensee = next;
    return this;
  }

  public LicenceRoute nextSite() {
    SortDirection next = next(site);
    clearSortDirections();
    this.site = next;
    return this;
  }

  public LicenceRoute nextRegistrationDate() {
    SortDirection next = next(registrationDate);
    clearSortDirections();
    this.registrationDate = next;
    return this;
  }

  public LicenceRoute setPage(int page) {
    this.page = page;
    return this;
  }

  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("/licences?tab=");
    stringBuilder.append(LicenceListTab.OGELS);
    if (reference != null) {
      stringBuilder.append("&reference=");
      stringBuilder.append(reference);
    }
    if (licensee != null) {
      stringBuilder.append("&licensee=");
      stringBuilder.append(licensee);
    }
    if (site != null) {
      stringBuilder.append("&site=");
      stringBuilder.append(site);
    }
    if (registrationDate != null) {
      stringBuilder.append("&date=");
      stringBuilder.append(registrationDate);
    }
    if (page != null && page != 1) {
      stringBuilder.append("&page=");
      stringBuilder.append(page);
    }
    return stringBuilder.toString();
  }

}
