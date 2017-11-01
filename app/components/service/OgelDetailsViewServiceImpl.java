package components.service;

import com.google.inject.Inject;
import components.client.OgelRegistrationServiceClient;
import components.client.OgelServiceClient;
import components.exceptions.ServiceException;
import models.view.OgelDetailsView;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.Optional;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private final OgelRegistrationServiceClient ogelRegistrationServiceClient;
  private final OgelServiceClient ogelServiceClient;

  @Inject
  public OgelDetailsViewServiceImpl(OgelRegistrationServiceClient ogelRegistrationServiceClient, OgelServiceClient ogelServiceClient) {
    this.ogelRegistrationServiceClient = ogelRegistrationServiceClient;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public OgelDetailsView getOgelDetailsView(String userId, String registrationReference) {
    List<OgelRegistrationView> ogelRegistrationViews = ogelRegistrationServiceClient.getOgelRegistrations(userId);
    Optional<OgelRegistrationView> ogelRegistrationView = ogelRegistrationViews.stream()
        .filter(orv -> orv.getRegistrationReference().equals(registrationReference))
        .findAny();
    if (ogelRegistrationView.isPresent()) {
      OgelFullView ogelFullView = ogelServiceClient.getOgel(ogelRegistrationView.get().getOgelType());
      return new OgelDetailsView(registrationReference, ogelFullView.getName(), ogelFullView.getLink(), ogelFullView.getSummary());
    } else {
      String message = String.format("Unable to find ogel licence with registration reference %s for user %s", registrationReference, userId);
      throw new ServiceException(message);
    }
  }

}
