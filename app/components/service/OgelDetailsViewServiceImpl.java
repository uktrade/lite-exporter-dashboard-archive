package components.service;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.common.client.OgelServiceClient;
import models.view.OgelDetailsView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.CompletionStage;

public class OgelDetailsViewServiceImpl implements OgelDetailsViewService {

  private final OgelServiceClient ogelServiceClient;
  private final String permissionsFinderUrl;

  @Inject
  public OgelDetailsViewServiceImpl(@Named("permissionsFinderUrl") String permissionsFinderUrl,
                                    OgelServiceClient ogelServiceClient) {
    this.permissionsFinderUrl = permissionsFinderUrl;
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public CompletionStage<OgelDetailsView> getOgelDetailsView(OgelRegistrationView ogelRegistrationView) {
    return ogelServiceClient.getById(ogelRegistrationView.getOgelType()).thenApply(ogelFullView -> {
      String viewLetterLink = String.format("%s/licencefinder/view-ogel?registrationRef=%s", permissionsFinderUrl,
          encode(ogelRegistrationView.getRegistrationReference()));
      return new OgelDetailsView(ogelRegistrationView.getRegistrationReference(),
          ogelFullView.getName(),
          ogelFullView.getLink(),
          viewLetterLink);
    });
  }

  private String encode(String queryParam) {
    try {
      return URLEncoder.encode(queryParam, "UTF-8");
    } catch (UnsupportedEncodingException uee) {
      throw new RuntimeException("Unable to encode " + queryParam, uee);
    }
  }

}
