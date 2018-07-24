package components.service;

import com.google.inject.Inject;
import com.spotify.futures.CompletableFutures;
import components.common.client.CustomerServiceClient;
import components.util.ApplicationUtil;
import components.util.LicenceUtil;
import models.view.SielDetailsView;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.concurrent.CompletionStage;

public class SielDetailsViewServiceImpl implements SielDetailsViewService {

  private final TimeService timeService;
  private final CustomerServiceClient customerServiceClient;

  @Inject
  public SielDetailsViewServiceImpl(TimeService timeService,
                                    CustomerServiceClient customerServiceClient) {
    this.timeService = timeService;
    this.customerServiceClient = customerServiceClient;
  }

  @Override
  public CompletionStage<SielDetailsView> getSielDetailsView(LicenceView licenceView) {
    CompletionStage<CustomerView> customerStage = customerServiceClient.getCustomer(licenceView.getCustomerId());
    CompletionStage<SiteView> siteStage = customerServiceClient.getSite(licenceView.getSiteId());
    return CompletableFutures.combine(customerStage, siteStage, (customerView, siteView) -> {
      String sielStatusName = LicenceUtil.getSielStatusName(licenceView.getStatus());
      String issueDate = timeService.formatDate(timeService.toMillis(licenceView.getIssueDate()));
      String expiryDate = timeService.formatDate(timeService.toMillis(licenceView.getExpiryDate()));
      String exportDestinations = ApplicationUtil.getSielDestinations(licenceView);
      return new SielDetailsView(licenceView.getLicenceRef(),
          licenceView.getOriginalExporterRef(),
          "SIEL Permanent",
          sielStatusName,
          issueDate,
          expiryDate,
          exportDestinations,
          siteView.getSiteName(),
          customerView.getCompanyName());
    });
  }

}
