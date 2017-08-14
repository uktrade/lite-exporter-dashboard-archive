package models.view;

import models.Page;
import models.view.route.LicenceRoute;

public class OgelRegistrationListView {

  private final String tab;
  private final String reference;
  private final String licensee;
  private final String site;
  private final String registrationDate;
  private final Page<OgelRegistrationItemView> page;

  public OgelRegistrationListView(Page<OgelRegistrationItemView> page, String tab, String reference, String licensee, String site, String registrationDate) {
    this.page = page;
    this.tab = tab;
    this.reference = reference;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
  }

  public Page<OgelRegistrationItemView> getPage() {
    return page;
  }

  public String getTab() {
    return tab;
  }

  public String getReference() {
    return reference;
  }

  public String getLicensee() {
    return licensee;
  }

  public String getSite() {
    return site;
  }

  public String getRegistrationDate() {
    return registrationDate;
  }

  public LicenceRoute getLicenceRoute() {
    return new LicenceRoute(tab, reference, licensee, site, registrationDate, page.getCurrentPage());
  }

}
