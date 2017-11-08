package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.SielDao;
import components.util.ApplicationUtil;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import java.util.List;
import java.util.Optional;
import models.Siel;
import models.view.SielDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SielDetailsViewServiceImpl implements SielDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SielDetailsViewServiceImpl.class);

  private final SielDao sielDao;
  private final UserPermissionService userPermissionService;
  private final CustomerServiceClient customerServiceClient;

  @Inject
  public SielDetailsViewServiceImpl(SielDao sielDao,
                                    UserPermissionService userPermissionService,
                                    CustomerServiceClient customerServiceClient) {
    this.sielDao = sielDao;
    this.userPermissionService = userPermissionService;
    this.customerServiceClient = customerServiceClient;
  }

  @Override
  public Optional<SielDetailsView> getSielDetailsView(String userId, String caseReference) {
    List<String> customerIds = userPermissionService.getCustomerIdsWithViewingPermission(userId);
    Optional<Siel> sielOptional = sielDao.getSiels(customerIds).stream()
        .filter(siel -> siel.getCaseReference().equals(caseReference))
        .findAny();
    if (sielOptional.isPresent()) {
      Siel siel = sielOptional.get();
      String sielStatusName = LicenceUtil.getSielStatusName(siel.getSielStatus());
      String issueDate = TimeUtil.formatDate(siel.getIssueTimestamp());
      String expiryDate = TimeUtil.formatDate(siel.getExpiryTimestamp());
      String exportDestinations = ApplicationUtil.getSielDestinations(siel);
      String site = customerServiceClient.getSite(siel.getSiteId()).getSiteName();
      String licensee = customerServiceClient.getCustomer(siel.getCustomerId()).getCompanyName();
      SielDetailsView sielDetailsView = new SielDetailsView(siel.getCaseReference(),
          siel.getApplicantReference(),
          "SIEL Permanent",
          sielStatusName,
          issueDate,
          expiryDate,
          exportDestinations,
          site,
          licensee);
      return Optional.of(sielDetailsView);
    } else {
      LOGGER.error("Unable to find siel licence with case reference {} for user {}", caseReference, userId);
      return Optional.empty();
    }
  }

}
