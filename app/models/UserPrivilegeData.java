package models;

import java.util.Set;

public class UserPrivilegeData {

  private Set<String> customerIds;
  private Set<String> siteIds;

  public UserPrivilegeData(Set<String> customerIds, Set<String> siteIds) {
    this.customerIds = customerIds;
    this.siteIds = siteIds;
  }

  public Set<String> getCustomerIds() {
    return customerIds;
  }

  public Set<String> getSiteIds() {
    return siteIds;
  }

}
