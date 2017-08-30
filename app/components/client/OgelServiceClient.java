package components.client;

import uk.gov.bis.lite.ogel.api.view.OgelFullView;

public interface OgelServiceClient {

  OgelFullView getOgel(String ogelId);

}
