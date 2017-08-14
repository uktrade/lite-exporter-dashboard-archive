package components.service;

import static components.util.RandomUtil.smallRandom;
import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.ArrayList;
import java.util.List;

public class OgelRegistrationServiceMockImpl implements OgelRegistrationService {

  private final TimeFormatService timeFormatService;
  private final List<OgelRegistrationView> ogelRegistrationViews;

  @Inject
  public OgelRegistrationServiceMockImpl(TimeFormatService timeFormatService) {
    this.timeFormatService = timeFormatService;
    this.ogelRegistrationViews = createOgelRegistrationViews();
  }

  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    return ogelRegistrationViews;
  }

  private List<OgelRegistrationView> createOgelRegistrationViews() {
    List<OgelRegistrationView> ogelRegistrationViews = new ArrayList<>();
    for (int i = 0; i < 26; i++) {
      OgelRegistrationView ogelRegistrationView = new OgelRegistrationView();
      ogelRegistrationView.setCustomerId(smallRandom("CLI"));
      ogelRegistrationView.setOgelType(smallRandom("OGE"));
      ogelRegistrationView.setRegistrationDate(timeFormatService.formatOgelRegistrationDate(time(2017, 2, 2 + i, 16, 20 + i)));
      ogelRegistrationView.setSiteId(smallRandom("SIT"));
      ogelRegistrationView.setRegistrationReference(smallRandom("REG"));
      ogelRegistrationView.setStatus(OgelRegistrationView.Status.UNKNOWN);
      ogelRegistrationViews.add(ogelRegistrationView);
    }
    return ogelRegistrationViews;
  }

}
