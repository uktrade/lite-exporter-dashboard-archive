package components.service;

import static com.spotify.futures.CompletableFutures.allAsList;

import com.google.inject.Inject;
import com.spotify.futures.CompletableFutures;
import components.common.client.CustomerServiceClient;
import components.common.client.OgelServiceClient;
import components.util.EnumUtil;
import components.util.LicenceUtil;
import components.util.MapUtil;
import models.view.OgelItemView;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

public class OgelItemViewServiceImpl implements OgelItemViewService {

  private final CustomerServiceClient customerServiceClient;
  private final OgelServiceClient ogelServiceClient;
  private final TimeService timeService;

  @Inject
  public OgelItemViewServiceImpl(CustomerServiceClient customerServiceClient,
                                 OgelServiceClient ogelServiceClient, TimeService timeService) {
    this.customerServiceClient = customerServiceClient;
    this.ogelServiceClient = ogelServiceClient;
    this.timeService = timeService;
  }

  @Override
  public CompletionStage<List<OgelItemView>> getOgelItemViews(List<OgelRegistrationView> ogelRegistrationViews) {
    List<CompletionStage<CustomerView>> customerStages = createCustomerStages(ogelRegistrationViews);
    List<CompletionStage<SiteView>> siteStages = createSiteStages(ogelRegistrationViews);
    List<CompletionStage<OgelFullView>> ogelStages = createOgelStages(ogelRegistrationViews);
    return CompletableFutures.combine(allAsList(customerStages), allAsList(siteStages), allAsList(ogelStages),
        (customerViews, siteViews, ogelFullViews) -> {
          Map<String, CustomerView> customerViewMap = MapUtil.createCustomerViewMap(customerViews);
          Map<String, SiteView> siteViewMap = MapUtil.createSiteViewMap(siteViews);
          Map<String, OgelFullView> ogelFullViewMap = MapUtil.createOgelFullViewMap(ogelFullViews);
          return ogelRegistrationViews.stream()
              .map(view -> getOgelItemView(view, customerViewMap.get(view.getCustomerId()),
                  siteViewMap.get(view.getSiteId()), ogelFullViewMap.get(view.getOgelType())))
              .collect(Collectors.toList());
        });
  }

  private List<CompletionStage<CustomerView>> createCustomerStages(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getCustomerId)
        .distinct()
        .map(customerServiceClient::getCustomer)
        .collect(Collectors.toList());
  }

  private List<CompletionStage<SiteView>> createSiteStages(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getSiteId)
        .distinct()
        .map(customerServiceClient::getSite)
        .collect(Collectors.toList());
  }

  private List<CompletionStage<OgelFullView>> createOgelStages(List<OgelRegistrationView> ogelRegistrationViews) {
    return ogelRegistrationViews.stream()
        .map(OgelRegistrationView::getOgelType)
        .distinct()
        .map(ogelServiceClient::getById)
        .collect(Collectors.toList());
  }

  private OgelItemView getOgelItemView(OgelRegistrationView ogelRegistrationView, CustomerView customerView,
                                       SiteView siteView, OgelFullView ogelFullView) {
    long registrationTimestamp = timeService.toMillis(timeService.parseYearMonthDate(ogelRegistrationView.getRegistrationDate()));
    String registrationDate = timeService.formatDate(registrationTimestamp);
    long updatedTimestamp;
    String updatedDate;
    if (ogelFullView.getLastUpdatedDate() != null) {
      updatedTimestamp = timeService.toMillis(ogelFullView.getLastUpdatedDate());
      updatedDate = timeService.formatDate(updatedTimestamp);
    } else {
      updatedTimestamp = 0;
      updatedDate = "-";
    }
    OgelRegistrationView.Status status = EnumUtil.parse(ogelRegistrationView.getStatus().toString(), OgelRegistrationView.Status.class, OgelRegistrationView.Status.UNKNOWN);
    String ogelStatusName = LicenceUtil.getOgelStatusName(status);
    return new OgelItemView(ogelRegistrationView.getRegistrationReference(),
        ogelFullView.getName(),
        customerView.getCompanyName(),
        siteView.getSiteName(),
        registrationDate,
        registrationTimestamp,
        updatedDate,
        updatedTimestamp,
        ogelStatusName);
  }

}
