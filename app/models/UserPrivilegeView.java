package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import models.enums.UserAccountType;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public class UserPrivilegeView {

  private UserAccountType userAccountType;
  private List<CustomerView> customers;
  private List<SiteView> sites;

  public UserPrivilegeView(@JsonProperty("userAccountType") UserAccountType userAccountType,
                           @JsonProperty("customers") List<CustomerView> customers,
                           @JsonProperty("sites") List<SiteView> sites) {
    this.userAccountType = userAccountType;
    this.customers = customers;
    this.sites = sites;
  }

  public UserAccountType getUserAccountType() {
    return userAccountType;
  }

  public List<CustomerView> getCustomers() {
    return customers;
  }

  public List<SiteView> getSites() {
    return sites;
  }

}
