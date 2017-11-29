package components.service;

import com.google.inject.Inject;
import components.client.LicenceClient;
import components.client.OgelServiceClient;
import java.util.Optional;
import models.view.OgelDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelDetailsViewServiceImpl.class);

  private final LicenceClient licenceClient;
  private final OgelServiceClient ogelServiceClient;

  @Inject
  public OgelDetailsViewServiceImpl(LicenceClient licenceClient, OgelServiceClient ogelServiceClient) {
    this.licenceClient = licenceClient;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public Optional<OgelDetailsView> getOgelDetailsView(String userId, String registrationReference) {
    OgelRegistrationView ogelRegistrationView;
    try {
      ogelRegistrationView = licenceClient.getOgelRegistration(userId, registrationReference);
    } catch (Exception exception) {
      LOGGER.error("Unable to find ogel licence with registration reference {} for user {}", registrationReference, userId, exception);
      return Optional.empty();
    }
    OgelFullView ogelFullView = ogelServiceClient.getOgel(ogelRegistrationView.getOgelType());
    OgelDetailsView ogelDetailsView = new OgelDetailsView(registrationReference,
        ogelFullView.getName(),
        ogelFullView.getLink(),
        ogelFullView.getSummary());
    return Optional.of(ogelDetailsView);
  }

}
