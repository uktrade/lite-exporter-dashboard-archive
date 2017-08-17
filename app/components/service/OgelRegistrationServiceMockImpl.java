package components.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import components.client.CustomerServiceClientImpl;
import components.client.PermissionsServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;

public class OgelRegistrationServiceMockImpl implements OgelRegistrationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelRegistrationServiceMockImpl.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  private final TimeFormatService timeFormatService;
  private final CustomerServiceClientImpl customerServiceClient;
  private final PermissionsServiceClient permissionsServiceClient;

  @Inject
  public OgelRegistrationServiceMockImpl(TimeFormatService timeFormatService,
                                         CustomerServiceClientImpl customerServiceClient,
                                         PermissionsServiceClient permissionsServiceClient) {
    this.timeFormatService = timeFormatService;
    this.customerServiceClient = customerServiceClient;
    this.permissionsServiceClient = permissionsServiceClient;
  }

  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    return null;
//    List<OgelRegistrationView> ogelRegistrationViews = permissionsServiceClient.getOgelRegistrations(userId);
//    Map<String, SiteView> siteViews = ogelRegistrationViews.stream()
//        .map(OgelRegistrationView::getSiteId)
//        .distinct()
//        .map(customerServiceClient::getSite)
//        .collect(Collectors.toMap(SiteView::getSiteId, Function.identity()));
//    Map<String, CustomerView> customerViews = ogelRegistrationViews.stream()
//        .map(OgelRegistrationView::getCustomerId)
//        .distinct()
//        .map(customerServiceClient::getCustomer)
//        .collect(Collectors.toMap(CustomerView::getCustomerId, Function.identity()));
//    // We expect exactly one registration view, but recycle it 25 times.
//    OgelRegistrationView ogelRegistrationView = ogelRegistrationViews.get(0);
//    List<OgelRegistrationView> recycledViews = new ArrayList<>();
//    for (int i = 0; i < 25; i++) {
//
//    }
//
//
//    SiteView siteView = customerServiceClient.getSite("SAR1_SITE1");
//    CustomerView customerView = customerServiceClient.getCustomer("SAR1");
//    try {
//      LOGGER.error(WRITER.writeValueAsString(ogelRegistrationViews));
//      LOGGER.error(WRITER.writeValueAsString(siteView));
//      LOGGER.error(WRITER.writeValueAsString(customerView));
//    } catch (JsonProcessingException e) {
//      e.printStackTrace();
//    }
//    return ogelRegistrationViews;
  }

//  private List<OgelRegistrationView> createOgelRegistrationViews() {
//    List<OgelRegistrationView> ogelRegistrationViews = new ArrayList<>();
//    for (int i = 0; i < 26; i++) {
//      OgelRegistrationView ogelRegistrationView = new OgelRegistrationView();
//      ogelRegistrationView.setCustomerId(smallRandom("CLI"));
//      ogelRegistrationView.setOgelType(smallRandom("OGE"));
//      ogelRegistrationView.setRegistrationDate(timeFormatService.formatOgelRegistrationDate(time(2017, 2, 2 + i, 16, 20 + i)));
//      ogelRegistrationView.setSiteId(smallRandom("SIT"));
//      ogelRegistrationView.setRegistrationReference(smallRandom("REG"));
//      ogelRegistrationView.setStatus(OgelRegistrationView.Status.UNKNOWN);
//      ogelRegistrationViews.add(ogelRegistrationView);
//    }
//    return ogelRegistrationViews;
//  }

}
