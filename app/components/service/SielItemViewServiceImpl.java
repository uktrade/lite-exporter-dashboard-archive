package components.service;

import static com.spotify.futures.CompletableFutures.allAsList;

import com.google.inject.Inject;
import com.spotify.futures.CompletableFutures;
import components.common.client.CustomerServiceClient;
import components.util.MapUtil;
import models.view.SielItemView;
import org.apache.commons.lang3.StringUtils;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class SielItemViewServiceImpl implements SielItemViewService {

  private final CustomerServiceClient customerServiceClient;
  private final TimeService timeService;

  @Inject
  public SielItemViewServiceImpl(CustomerServiceClient customerServiceClient,
                                 TimeService timeService) {
    this.customerServiceClient = customerServiceClient;
    this.timeService = timeService;
  }

  @Override
  public CompletionStage<List<SielItemView>> getSielItemViews(List<LicenceView> licenceViews) {
    List<CompletionStage<CustomerView>> customerStages = createCustomerStages(licenceViews);
    List<CompletionStage<SiteView>> siteStages = createSiteStages(licenceViews);
    return CompletableFutures.combine(allAsList(customerStages), allAsList(siteStages), (customerViews, siteViews) -> {
      Map<String, CustomerView> customerViewMap = MapUtil.createCustomerViewMap(customerViews);
      Map<String, SiteView> siteViewMap = MapUtil.createSiteViewMap(siteViews);
      return licenceViews.stream().map(licenceView -> {
        String companyName = customerViewMap.get(licenceView.getCustomerId()).getCompanyName();
        String siteName = siteViewMap.get(licenceView.getSiteId()).getSiteName();
        return createSielItemView(licenceView, companyName, siteName);
      }).collect(Collectors.toList());
    });
  }

  private List<CompletionStage<CustomerView>> createCustomerStages(List<LicenceView> licenceViews) {
    return licenceViews.stream()
        .map(LicenceView::getCustomerId)
        .distinct()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toList());
  }

  private List<CompletionStage<SiteView>> createSiteStages(List<LicenceView> licenceViews) {
    return licenceViews.stream()
        .map(LicenceView::getSiteId)
        .distinct()
        .map(customerServiceClient::getSite)
        .collect(Collectors.toList());
  }

  private SielItemView createSielItemView(LicenceView licenceView, String companyName, String siteName) {
    long expiryTimestamp = timeService.toMillis(licenceView.getExpiryDate());
    String expiryDate = timeService.formatDate(expiryTimestamp);
    String sielStatus = StringUtils.capitalize(licenceView.getStatus().toString().toLowerCase());
    return new SielItemView(licenceView.getLicenceRef(),
        licenceView.getOriginalExporterRef(),
        companyName,
        siteName,
        expiryDate,
        expiryTimestamp,
        sielStatus);
  }

}
