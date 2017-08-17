package components.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.inject.Inject;
import components.client.OgelServiceClient;
import components.client.PermissionsServiceClient;
import components.exceptions.ServiceException;
import models.view.OgelDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;
import java.util.Optional;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelDetailsViewServiceImpl.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  private final PermissionsServiceClient permissionsServiceClient;
  private final OgelServiceClient ogelServiceClient;

  @Inject
  public OgelDetailsViewServiceImpl(PermissionsServiceClient permissionsServiceClient, OgelServiceClient ogelServiceClient) {
    this.permissionsServiceClient = permissionsServiceClient;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public OgelDetailsView getOgelDetailsView(String userId, String registrationReference) {
    // TODO Is there a way to get the ogel directly?
    List<OgelRegistrationView> ogelRegistrationViews = permissionsServiceClient.getOgelRegistrations(userId);
    Optional<OgelRegistrationView> ogelRegistrationView = ogelRegistrationViews.stream().filter(orv -> orv.getRegistrationReference().equals(registrationReference)).findAny();
    try {
      LOGGER.error(WRITER.writeValueAsString(ogelRegistrationViews));
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    if (ogelRegistrationView.isPresent()) {
      OgelFullView ogelFullView = ogelServiceClient.getOgel(ogelRegistrationView.get().getOgelType());
      return new OgelDetailsView(registrationReference, ogelFullView.getName(), ogelFullView.getLink(), ogelFullView.getSummary());
    } else {
      String message = String.format("Unable to find ogel license with registration reference %s", registrationReference);
      throw new ServiceException(message);
    }
  }

}
