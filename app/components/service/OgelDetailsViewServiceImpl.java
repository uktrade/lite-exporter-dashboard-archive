package components.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.cache.LicenceClientCache;
import components.cache.OgelServiceClientCache;
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

  private final LicenceClientCache licenceClientCache;
  private final OgelServiceClientCache ogelServiceClientCache;
  private final String permissionsFinderUrl;

  @Inject
  public OgelDetailsViewServiceImpl(@Named("permissionsFinderUrl") String permissionsFinderUrl,
                                    LicenceClientCache licenceClientCache,
                                    OgelServiceClientCache ogelServiceClientCache) {
    this.permissionsFinderUrl = permissionsFinderUrl;
    this.licenceClientCache = licenceClientCache;
    this.ogelServiceClientCache = ogelServiceClientCache;
  }

  @Override
  public Optional<OgelDetailsView> getOgelDetailsView(String userId, String registrationReference) {
    OgelRegistrationView ogelRegistrationView;
    try {
      ogelRegistrationView = licenceClientCache.getOgelRegistration(userId, registrationReference);
    } catch (Exception exception) {
      LOGGER.error("Unable to find ogel licence with registration reference {} for user {}", registrationReference, userId, exception);
      return Optional.empty();
    }
    OgelFullView ogelFullView = ogelServiceClientCache.getOgel(ogelRegistrationView.getOgelType());
    String viewLetterLink = String.format("%s/licencefinder/view-ogel?registrationRef=%s", permissionsFinderUrl,
        encode(ogelRegistrationView.getRegistrationReference()));
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
