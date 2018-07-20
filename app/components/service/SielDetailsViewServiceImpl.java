package components.service;

import com.google.inject.Inject;
import components.cache.CustomerServiceClientCache;
import components.cache.LicenceClientCache;
import components.exceptions.ServiceException;
import components.util.ApplicationUtil;
import components.util.LicenceUtil;
import models.view.SielDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.permissions.api.view.LicenceView;

import java.util.Optional;

public class SielDetailsViewServiceImpl implements SielDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SielDetailsViewServiceImpl.class);

  private final CustomerServiceClientCache customerServiceClientCache;
  private final LicenceClientCache licenceClientCache;
  private final TimeService timeService;

  @Inject
  public SielDetailsViewServiceImpl(CustomerServiceClientCache customerServiceClientCache,
                                    LicenceClientCache licenceClientCache, TimeService timeService) {
    this.customerServiceClientCache = customerServiceClientCache;
    this.licenceClientCache = licenceClientCache;
    this.timeService = timeService;
  }

  @Override
  public Optional<SielDetailsView> getSielDetailsView(String userId, String reference) {
    LicenceView licenceView;
    try {
      licenceView = licenceClientCache.getLicence(userId, reference);
    } catch (ServiceException serviceException) {
      LOGGER.error("Unable to find siel licence with reference {} for user {}", reference, userId);
      return Optional.empty();
    }
    String sielStatusName = LicenceUtil.getSielStatusName(licenceView.getStatus());
    String issueDate = timeService.formatDate(timeService.toMillis(licenceView.getIssueDate()));
    String expiryDate = timeService.formatDate(timeService.toMillis(licenceView.getExpiryDate()));
    String exportDestinations = ApplicationUtil.getSielDestinations(licenceView);
    String site = customerServiceClientCache.getSite(licenceView.getSiteId()).getSiteName();
    String licensee = customerServiceClientCache.getCustomer(licenceView.getCustomerId()).getCompanyName();
    SielDetailsView sielDetailsView = new SielDetailsView(licenceView.getLicenceRef(),
        licenceView.getOriginalExporterRef(),
        "SIEL Permanent",
        sielStatusName,
        issueDate,
        expiryDate,
        exportDestinations,
        site,
        licensee);
    return Optional.of(sielDetailsView);
  }

}
