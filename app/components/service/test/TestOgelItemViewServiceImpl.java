package components.service.test;

import static components.util.TimeUtil.time;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.client.OgelServiceClient;
import components.client.PermissionsServiceClient;
import components.service.OgelItemViewServiceImpl;
import components.util.LicenceUtil;
import components.util.TimeUtil;
import models.enums.OgelStatus;
import models.view.OgelItemView;

import java.util.ArrayList;
import java.util.List;

public class TestOgelItemViewServiceImpl extends OgelItemViewServiceImpl {

  @Inject
  public TestOgelItemViewServiceImpl(PermissionsServiceClient permissionsServiceClient,
                                     CustomerServiceClient customerServiceClient,
                                     OgelServiceClient ogelServiceClient) {
    super(permissionsServiceClient, customerServiceClient, ogelServiceClient);
  }

  @Override
  public List<OgelItemView> getOgelItemViews(String userId) {
    List<OgelItemView> ogelItemViews = super.getOgelItemViews(userId);
    return recycleOgelItemView(ogelItemViews.get(0));
  }

  private List<OgelItemView> recycleOgelItemView(OgelItemView base) {
    List<OgelItemView> recycledViews = new ArrayList<>();
    for (int i = 1; i < 22; i++) {
      String add = i % 2 == 0 ? "_A" : "_B";
      long registrationTimestamp = time(2017, 2, 2 + i, 16, 20 + i);
      String registrationDate = TimeUtil.formatDateWithSlashes(registrationTimestamp);
      OgelStatus ogelStatus = OgelStatus.values()[i % (OgelStatus.values().length - 1)];
      String ogelStatusName = LicenceUtil.getOgelStatusName(ogelStatus);
      OgelItemView ogelItemView = new OgelItemView(base.getRegistrationReference(),
          base.getDescription(),
          base.getLicensee() + add,
          base.getSite() + add,
          registrationDate, registrationTimestamp,
          ogelStatusName);
      recycledViews.add(ogelItemView);
    }
    return recycledViews;
  }

}
