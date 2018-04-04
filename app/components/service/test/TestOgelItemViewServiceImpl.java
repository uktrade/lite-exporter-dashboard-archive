package components.service.test;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import components.client.LicenceClient;
import components.client.OgelServiceClient;
import components.service.OgelItemViewServiceImpl;
import components.service.TimeService;
import components.util.LicenceUtil;
import models.view.OgelItemView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.ArrayList;
import java.util.List;

public class TestOgelItemViewServiceImpl extends OgelItemViewServiceImpl {

  private final TimeService timeService;

  @Inject
  public TestOgelItemViewServiceImpl(LicenceClient licenceClient, CustomerServiceClient customerServiceClient,
                                     OgelServiceClient ogelServiceClient, TimeService timeService) {
    super(licenceClient, customerServiceClient, ogelServiceClient, timeService);
    this.timeService = timeService;
  }

  @Override
  public List<OgelItemView> getOgelItemViews(String userId) {
    List<OgelItemView> ogelItemViews = super.getOgelItemViews(userId);
    if (ogelItemViews.size() == 1) {
      return recycleOgelItemView(ogelItemViews.get(0));
    } else {
      return new ArrayList<>();
    }
  }

  private List<OgelItemView> recycleOgelItemView(OgelItemView base) {
    List<OgelItemView> recycledViews = new ArrayList<>();
    for (int i = 1; i < 22; i++) {
      String add = i % 2 == 0 ? "_A" : "_B";
      long registrationTimestamp = timeService.time(2017, 2, 2 + i, 16, 20 + i);
      String registrationDate = timeService.formatDate(registrationTimestamp);
      long updatedTimestamp;
      String updatedDate;
      if (i % 4 == 0) {
        updatedTimestamp = 0;
        updatedDate = "-";
      } else {
        updatedTimestamp = timeService.time(2017, 3, 3 + i, 16, 20 + i);
        updatedDate = timeService.formatDate(updatedTimestamp);
      }
      OgelRegistrationView.Status status = OgelRegistrationView.Status.values()[i % (OgelRegistrationView.Status.values().length - 1)];
      String ogelStatusName = LicenceUtil.getOgelStatusName(status);
      String registrationReference;
      if (base.getRegistrationReference().startsWith("GBOGE2017/12345")) {
        registrationReference = base.getRegistrationReference() + Strings.padStart("" + i, 2, '0');
      } else {
        registrationReference = base.getRegistrationReference();
      }
      OgelItemView ogelItemView = new OgelItemView(registrationReference,
          base.getDescription(),
          base.getLicensee() + add,
          base.getSite() + add,
          registrationDate,
          registrationTimestamp,
          updatedDate,
          updatedTimestamp,
          ogelStatusName);
      recycledViews.add(ogelItemView);
    }
    return recycledViews;
  }

}
