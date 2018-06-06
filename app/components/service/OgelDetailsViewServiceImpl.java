package components.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.LicenceClient;
import components.client.OgelServiceClient;
import models.view.OgelDetailsView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Optional;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private static final Logger LOGGER = LoggerFactory.getLogger(OgelDetailsViewServiceImpl.class);

  private final LicenceClient licenceClient;
  private final OgelServiceClient ogelServiceClient;
  private final String permissionsFinderUrl;

  @Inject
  public OgelDetailsViewServiceImpl(@Named("permissionsFinderUrl") String permissionsFinderUrl,
                                    LicenceClient licenceClient, OgelServiceClient ogelServiceClient) {
    this.permissionsFinderUrl = permissionsFinderUrl;
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
    String viewLetterLink = String.format("%s/licencefinder/view-ogel/%s", permissionsFinderUrl, encode(ogelRegistrationView.getRegistrationReference()));
    OgelDetailsView ogelDetailsView = new OgelDetailsView(registrationReference,
        ogelFullView.getName(),
        ogelFullView.getLink(),
        viewLetterLink);
    return Optional.of(ogelDetailsView);
  }

  private String encode(String queryParam) {
    try {
      return URLEncoder.encode(queryParam, "UTF-8");
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("Unable to encode " + queryParam, uee);
    }
  }

}
