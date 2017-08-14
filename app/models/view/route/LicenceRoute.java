package models.view.route;

public class LicenceRoute {

  private String tab;
  private String reference;
  private String licensee;
  private String site;
  private String registrationDate;
  private Integer page;

  public LicenceRoute(String tab, String reference, String licensee, String site, String registrationDate, Integer page) {
    this.tab = tab;
    this.reference = reference;
    this.licensee = licensee;
    this.site = site;
    this.registrationDate = registrationDate;
    this.page = page;
  }

  private String next(String sortDirection) {
    if ("desc".equals(sortDirection)) {
      return "asc";
    } else {
      return "desc";
    }
  }

  private void clearSortDirections() {
    reference = null;
    licensee = null;
    site = null;
    registrationDate = null;
  }

  public LicenceRoute nextReference() {
    String next = next(reference);
    clearSortDirections();
    this.reference = next;
    return this;
  }

  public LicenceRoute nextLicensee() {
    String next = next(licensee);
    clearSortDirections();
    this.licensee = next;
    return this;
  }

  public LicenceRoute nextSite() {
    String next = next(site);
    clearSortDirections();
    this.site = next;
    return this;
  }

  public LicenceRoute nextRegistrationDate() {
    String next = next(registrationDate);
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
    if (tab != null) {
      stringBuilder.append("&tab=" + tab);
    }
    if (reference != null) {
      stringBuilder.append("&reference=" + reference);
    }
    if (licensee != null) {
      stringBuilder.append("&licensee=" + licensee);
    }
    if (site != null) {
      stringBuilder.append("&site=" + site);
    }
    if (registrationDate != null) {
      stringBuilder.append("&date=" + registrationDate);
    }
    if (page != null && page != 1) {
      stringBuilder.append("&page=" + page);
    }
    String url = stringBuilder.toString();
    if (url.startsWith("&")) {
      url = "?" + url.substring(1, url.length());
    }
    return "/licences" + url;
  }

}
