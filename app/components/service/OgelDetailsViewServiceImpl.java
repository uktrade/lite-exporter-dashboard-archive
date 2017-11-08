package components.service;

import com.google.inject.Inject;
import components.client.OgelRegistrationServiceClient;
import components.client.OgelServiceClient;
import java.util.List;
import java.util.Optional;
import models.view.OgelDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelDetailsViewServiceImpl.class);

  private final OgelRegistrationServiceClient ogelRegistrationServiceClient;
  private final OgelServiceClient ogelServiceClient;

  @Inject
  public OgelDetailsViewServiceImpl(OgelRegistrationServiceClient ogelRegistrationServiceClient, OgelServiceClient ogelServiceClient) {
    this.ogelRegistrationServiceClient = ogelRegistrationServiceClient;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public Optional<OgelDetailsView> getOgelDetailsView(String userId, String registrationReference) {
    List<OgelRegistrationView> ogelRegistrationViews = ogelRegistrationServiceClient.getOgelRegistrations(userId);
    Optional<OgelRegistrationView> ogelRegistrationView = ogelRegistrationViews.stream()
        .filter(orv -> orv.getRegistrationReference().equals(registrationReference))
        .findAny();
    if (ogelRegistrationView.isPresent()) {
      OgelFullView ogelFullView = ogelServiceClient.getOgel(ogelRegistrationView.get().getOgelType());
      OgelDetailsView ogelDetailsView = new OgelDetailsView(registrationReference,
          ogelFullView.getName(),
          ogelFullView.getLink(),
          ogelFullView.getSummary());
      return Optional.of(ogelDetailsView);
    } else {
      LOGGER.error("Unable to find ogel licence with registration reference {} for user {}", registrationReference, userId);
      return Optional.empty();
    }
  }

}
