package components.service.test;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import components.client.LicenceClient;
import components.client.OgelServiceClient;
import components.service.OgelDetailsViewServiceImpl;
import models.view.OgelDetailsView;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class TestOgelDetailsViewServiceImpl extends OgelDetailsViewServiceImpl {

  private static final Set<String> ENDINGS = new HashSet<>();

  static {
    for (int i = 1; i < 22; i++) {
      ENDINGS.add(Strings.padStart("" + i, 2, '0'));
    }
  }

  @Inject
  public TestOgelDetailsViewServiceImpl(LicenceClient licenceClient, OgelServiceClient ogelServiceClient) {
    super(licenceClient, ogelServiceClient);
  }

  @Override
  public Optional<OgelDetailsView> getOgelDetailsView(String userId, String registrationReference) {
    if (registrationReference != null &&
        registrationReference.length() == 17 &&
        registrationReference.startsWith("GBOGE2017/12345") &&
        ENDINGS.stream().anyMatch(ending -> StringUtils.endsWith(registrationReference, ending))) {
      Optional<OgelDetailsView> ogelDetailsView = super.getOgelDetailsView(userId, "GBOGE2017/12345");
      if (ogelDetailsView.isPresent()) {
        return Optional.of(new OgelDetailsView(registrationReference,
            ogelDetailsView.get().getName(),
            ogelDetailsView.get().getLink(),
            ogelDetailsView.get().getOgelConditionSummary()));
      } else {
        return Optional.empty();
      }
    } else {
      return super.getOgelDetailsView(userId, registrationReference);
    }
  }

}
