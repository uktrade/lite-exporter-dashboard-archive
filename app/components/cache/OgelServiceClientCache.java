package components.cache;

import uk.gov.bis.lite.ogel.api.view.OgelFullView;

public interface OgelServiceClientCache {

  OgelFullView getOgel(String ogelId);

}
