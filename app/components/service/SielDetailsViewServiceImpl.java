package components.service;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.dao.SielDao;
import components.util.ApplicationUtil;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import models.Siel;
import models.view.SielDetailsView;

public class SielDetailsViewServiceImpl implements SielDetailsViewService {

  private final SielDao sielDao;
  private final CustomerServiceClient customerServiceClient;

  @Inject
  public SielDetailsViewServiceImpl(SielDao sielDao, CustomerServiceClient customerServiceClient) {
    this.sielDao = sielDao;
    this.customerServiceClient = customerServiceClient;
  }

  @Override
  public SielDetailsView getSielDetailsView(String caseReference) {
    Siel siel = sielDao.getSiel(caseReference);
    String sielStatusName = LicenceUtil.getSielStatusName(siel.getSielStatus());
    String issueDate = TimeUtil.formatDate(siel.getIssueTimestamp());
    String expiryDate = TimeUtil.formatDate(siel.getExpiryTimestamp());
    String exportDestinations = ApplicationUtil.getDestinations(siel.getDestinationList());
    String site = customerServiceClient.getSite(siel.getSiteId()).getSiteName();
    String licensee = customerServiceClient.getCustomer(siel.getCompanyId()).getCompanyName();
    return new SielDetailsView(siel.getCaseReference(),
        siel.getApplicantReference(),
        "SIEL Permanent",
        sielStatusName,
        issueDate,
        expiryDate,
        exportDestinations,
        site,
        licensee);
  }

}
