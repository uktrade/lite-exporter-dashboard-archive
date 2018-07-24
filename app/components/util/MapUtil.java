package components.util;


import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {

  public static Map<String, OgelFullView> createOgelFullViewMap(List<OgelFullView> ogelFullViews) {
    return ogelFullViews.stream()
        .collect(Collectors.toMap(OgelFullView::getId, Function.identity()));
  }

  public static Map<String, SiteView> createSiteViewMap(List<SiteView> siteViews) {
    return siteViews.stream()
        .collect(Collectors.toMap(SiteView::getSiteId, Function.identity()));
  }

  public static Map<String, CustomerView> createCustomerViewMap(List<CustomerView> customerViews) {
    return customerViews.stream()
        .collect(Collectors.toMap(CustomerView::getCustomerId, Function.identity()));
  }

}
