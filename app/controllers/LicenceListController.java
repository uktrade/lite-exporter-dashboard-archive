package controllers;

import com.google.inject.Inject;
import components.cache.SessionCache;
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
import models.enums.LicenceListTab;
import models.enums.LicenceSortType;
import models.enums.SortDirection;
import models.view.LicenceListView;
import models.view.OgelDetailsView;
import models.view.OgelItemView;
import models.view.SielDetailsView;
import models.view.SielItemView;
import play.mvc.Result;
import views.html.licenceList;
import views.html.ogelDetails;
import views.html.sielDetails;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public class LicenceListController extends SamlController {

  private static final EnumSet<LicenceSortType> OGEL_ONLY_SORT_TYPES = EnumSet.of(LicenceSortType.REGISTRATION_DATE, LicenceSortType.LAST_UPDATED);
  private static final EnumSet<LicenceSortType> SIEL_ONLY_SORT_TYPES = EnumSet.of(LicenceSortType.EXPIRY_DATE);

  private final OgelItemViewService ogelItemViewService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final UserService userService;
  private final SielItemViewService sielItemViewService;
  private final SielDetailsViewService sielDetailsViewService;
  private final licenceList licenceList;
  private final ogelDetails ogelDetails;
  private final sielDetails sielDetails;

  @Inject
  public LicenceListController(OgelItemViewService ogelItemViewService,
                               OgelDetailsViewService ogelDetailsViewService,
                               UserService userService,
                               SielItemViewService sielItemViewService,
                               SielDetailsViewService sielDetailsViewService,
                               licenceList licenceList,
                               ogelDetails ogelDetails,
                               sielDetails sielDetails) {
    this.ogelItemViewService = ogelItemViewService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.userService = userService;
    this.sielItemViewService = sielItemViewService;
    this.sielDetailsViewService = sielDetailsViewService;
    this.licenceList = licenceList;
    this.ogelDetails = ogelDetails;
    this.sielDetails = sielDetails;
  }

  public Result licenceList(String tab, String sort, String direction, Integer page) {
    String userId = userService.getCurrentUserId();

    LicenceListState state = SessionCache.getLicenseListState(tab, sort, direction, page);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.ASC);
    LicenceSortType licenceSortType = EnumUtil.parse(state.getSort(), LicenceSortType.class, LicenceSortType.STATUS);
    LicenceListTab licenceListTab = EnumUtil.parse(state.getTab(), LicenceListTab.class, LicenceListTab.SIELS);

    boolean hasOgels = ogelItemViewService.hasOgelItemViews(userId);
    boolean hasSiels = sielItemViewService.hasSielItemViews(userId);

    if (licenceListTab == LicenceListTab.SIELS && !hasSiels && hasOgels) {
      licenceListTab = LicenceListTab.OGELS;
    } else if (licenceListTab == LicenceListTab.OGELS && !hasOgels && hasSiels) {
      licenceListTab = LicenceListTab.SIELS;
    }

    if ((licenceListTab == LicenceListTab.SIELS && OGEL_ONLY_SORT_TYPES.contains(licenceSortType)) ||
        (licenceListTab == LicenceListTab.OGELS && SIEL_ONLY_SORT_TYPES.contains(licenceSortType))) {
      licenceSortType = LicenceSortType.REFERENCE;
      sortDirection = SortDirection.ASC;
    }

    Page<OgelItemView> ogelPage = null;
    Page<SielItemView> sielPage = null;
    int currentPage;
    if (licenceListTab == LicenceListTab.OGELS) {
      List<OgelItemView> ogelItemViews = ogelItemViewService.getOgelItemViews(userId);
      SortUtil.sortOgels(ogelItemViews, licenceSortType, sortDirection);
      ogelPage = PageUtil.getPage(page, ogelItemViews);
      currentPage = ogelPage.getCurrentPage();
    } else {
      List<SielItemView> sielItemViews = sielItemViewService.getSielItemViews(userId);
      SortUtil.sortSiels(sielItemViews, licenceSortType, sortDirection);
      sielPage = PageUtil.getPage(page, sielItemViews);
      currentPage = sielPage.getCurrentPage();
    }

    LicenceListView licenceListView = new LicenceListView(hasSiels, hasOgels, licenceListTab, licenceSortType, sortDirection, ogelPage, sielPage, currentPage);

    return ok(licenceList.render(licenceListView));
  }

  public Result ogelDetails(String registrationReference) {
    String userId = userService.getCurrentUserId();
    Optional<OgelDetailsView> ogelDetailsView = ogelDetailsViewService.getOgelDetailsView(userId, registrationReference);
    if (ogelDetailsView.isPresent()) {
      return ok(ogelDetails.render(ogelDetailsView.get()));
    } else {
      return notFound("Unknown ogel.");
    }
  }

  public Result sielDetails(String registrationReference) {
    String userId = userService.getCurrentUserId();
    Optional<SielDetailsView> sielDetailsView = sielDetailsViewService.getSielDetailsView(userId, registrationReference);
    if (sielDetailsView.isPresent()) {
      return ok(sielDetails.render(sielDetailsView.get()));
    } else {
      return notFound("Unknown siel.");
    }
  }

}
