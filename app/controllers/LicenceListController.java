package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.cache.SessionCache;
import components.client.CustomerServiceClient;
import components.dao.ApplicationDao;
import components.service.OgelDetailsViewService;
import components.service.OgelItemViewService;
import components.service.SielDetailsViewService;
import components.service.SielItemViewService;
import components.service.UserService;
import components.util.EnumUtil;
import components.util.PageUtil;
import components.util.SortUtil;
import models.LicenceListState;
import models.Page;
import models.User;
import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.LicenceListView;
import models.view.OgelDetailsView;
import models.view.OgelItemView;
import models.view.SielDetailsView;
import models.view.SielItemView;
import play.mvc.Result;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import views.html.licenceList;
import views.html.ogelDetails;
import views.html.sielDetails;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

public class LicenceListController extends SamlController {

  private static final EnumSet<LicenceSortType> OGEL_ONLY_SORT_TYPES = EnumSet.of(LicenceSortType.REGISTRATION_DATE, LicenceSortType.SITE);
  private static final EnumSet<LicenceSortType> SIEL_ONLY_SORT_TYPES = EnumSet.of(LicenceSortType.EXPIRY_DATE);

  private final String licenceApplicationAddress;
  private final OgelItemViewService ogelItemViewService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final UserService userService;
  private final CustomerServiceClient customerServiceClient;
  private final ApplicationDao applicationDao;
  private final SielItemViewService sielItemViewService;
  private final SielDetailsViewService sielDetailsViewService;

  @Inject
  public LicenceListController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                               OgelItemViewService ogelItemViewService,
                               OgelDetailsViewService ogelDetailsViewService,
                               UserService userService,
                               CustomerServiceClient customerServiceClient,
                               ApplicationDao applicationDao,
                               SielItemViewService sielItemViewService,
                               SielDetailsViewService sielDetailsViewService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.ogelItemViewService = ogelItemViewService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.userService = userService;
    this.customerServiceClient = customerServiceClient;
    this.applicationDao = applicationDao;
    this.sielItemViewService = sielItemViewService;
    this.sielDetailsViewService = sielDetailsViewService;
  }

  public Result licenceList(String tab, String sort, String direction, Integer page) {
    String userId = userService.getCurrentUserId();

    LicenceListState state = SessionCache.getLicenseListState(tab, sort, direction, page);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.ASC);
    LicenceSortType licenceSortType = EnumUtil.parse(state.getSort(), LicenceSortType.class, LicenceSortType.STATUS);
    LicenceListTab licenceListTab = EnumUtil.parse(state.getTab(), LicenceListTab.class, LicenceListTab.SIELS);

    if ((licenceListTab == LicenceListTab.SIELS && OGEL_ONLY_SORT_TYPES.contains(licenceSortType)) ||
        (licenceListTab == LicenceListTab.OGELS && SIEL_ONLY_SORT_TYPES.contains(licenceSortType))) {
      licenceSortType = LicenceSortType.STATUS;
      sortDirection = SortDirection.ASC;
    }

    Page<OgelItemView> ogelPage = null;
    Page<SielItemView> sielPage = null;
    int currentPage;
    if (licenceListTab == LicenceListTab.OGELS) {
      ogelPage = getOgelPage(userId, licenceSortType, sortDirection, state.getPage());
      currentPage = ogelPage.getCurrentPage();
    } else {
      sielPage = getSielPage(userId, licenceSortType, sortDirection, state.getPage());
      currentPage = sielPage.getCurrentPage();
    }

    LicenceListView licenceListView = new LicenceListView(licenceListTab, licenceSortType, sortDirection, ogelPage, sielPage, currentPage);

    return ok(licenceList.render(licenceApplicationAddress, licenceListView));
  }

  private Page<OgelItemView> getOgelPage(String userId, LicenceSortType licenceSortType, SortDirection sortDirection, Integer page) {
    List<OgelItemView> ogelItemViews;
    if (isShowLicences()) {
      ogelItemViews = ogelItemViewService.getOgelItemViews(userId);
    } else {
      ogelItemViews = new ArrayList<>();
    }
    SortUtil.sortOgels(ogelItemViews, licenceSortType, sortDirection);
    return PageUtil.getPage(page, ogelItemViews);
  }

  private Page<SielItemView> getSielPage(String userId, LicenceSortType licenceSortType, SortDirection sortDirection, Integer page) {
    List<SielItemView> sielItemViews;
    if (isShowLicences()) {
      sielItemViews = sielItemViewService.getSielItemViews(userId);
    } else {
      sielItemViews = new ArrayList<>();
    }
    SortUtil.sortSiels(sielItemViews, licenceSortType, sortDirection);
    return PageUtil.getPage(page, sielItemViews);
  }

  public Result ogelDetails(String registrationReference) {
    String userId = userService.getCurrentUserId();
    OgelDetailsView ogelDetailsView = ogelDetailsViewService.getOgelDetailsView(userId, registrationReference);
    return ok(ogelDetails.render(licenceApplicationAddress, ogelDetailsView));
  }

  public Result sielDetails(String registrationReference) {
    SielDetailsView sielDetailsView = sielDetailsViewService.getSielDetailsView(registrationReference);
    return ok(sielDetails.render(licenceApplicationAddress, sielDetailsView));
  }

  // This is a hack for testing. We only show OGELS or SIELS if there is at least one application.
  private boolean isShowLicences() {
    String userId = userService.getCurrentUserId();
    List<String> customerViews = customerServiceClient.getCustomers(userId).stream()
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
    return !applicationDao.getApplications(customerViews).isEmpty();
  }

}
