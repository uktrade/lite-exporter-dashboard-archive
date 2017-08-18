package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.service.CacheService;
import components.service.OgelDetailsViewService;
import components.service.OgelRegistrationItemViewService;
import components.service.PageService;
import components.service.UserService;
import components.util.EnumUtil;
import models.LicenseListState;
import models.Page;
import models.User;
import models.enums.LicenceListTab;
import models.enums.SortDirection;
import models.view.OgelDetailsView;
import models.view.OgelRegistrationItemView;
import models.view.OgelRegistrationListView;
import play.mvc.Controller;
import play.mvc.Result;
import scala.Option;
import views.html.licenceDetails;
import views.html.licenceList;
import views.html.ogelDetails;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class LicenceListController extends Controller {

  private final CacheService cacheService;
  private final OgelRegistrationItemViewService ogelRegistrationItemViewService;
  private final PageService pageService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final UserService userService;
  private final String licenceApplicationAddress;

  @Inject
  public LicenceListController(CacheService cacheService,
                               OgelRegistrationItemViewService ogelRegistrationItemViewService,
                               PageService pageService, OgelDetailsViewService ogelDetailsViewService,
                               UserService userService,
                               @Named("licenceApplicationAddress") String licenceApplicationAddress) {
    this.cacheService = cacheService;
    this.ogelRegistrationItemViewService = ogelRegistrationItemViewService;
    this.pageService = pageService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.userService = userService;
    this.licenceApplicationAddress = licenceApplicationAddress;
  }

  public Result licenceList(Option<String> tab, Option<String> reference, Option<String> licensee, Option<String> site, Option<String> date, Option<Integer> page) {
    User currentUser = userService.getCurrentUser();

    LicenseListState state = cacheService.getLicenseListState(tab, reference, licensee, site, date, page);

    SortDirection referenceSortDirection = EnumUtil.parse(state.getReference(), SortDirection.class);
    SortDirection licenseeSortDirection = EnumUtil.parse(state.getLicensee(), SortDirection.class);
    SortDirection siteSortDirection = EnumUtil.parse(state.getSite(), SortDirection.class);
    SortDirection dateSortDirection = EnumUtil.parse(state.getDate(), SortDirection.class);

    long definedCount = Stream.of(referenceSortDirection, licenseeSortDirection, siteSortDirection, dateSortDirection).filter(Objects::nonNull).count();
    if (definedCount != 1) {
      referenceSortDirection = SortDirection.DESC;
      licenseeSortDirection = null;
      siteSortDirection = null;
      dateSortDirection = null;
    }

    LicenceListTab licenceListTab = EnumUtil.parse(state.getTab(), LicenceListTab.class, LicenceListTab.OGELS);

    Page<OgelRegistrationItemView> pageData = null;
    if (licenceListTab == LicenceListTab.OGELS) {
      List<OgelRegistrationItemView> ogelRegistrationItemViews = ogelRegistrationItemViewService.getOgelRegistrationItemViews(currentUser.getId(), referenceSortDirection, licenseeSortDirection, siteSortDirection, dateSortDirection);
      pageData = pageService.getPage(state.getPage(), ogelRegistrationItemViews);
    }

    OgelRegistrationListView ogelRegistrationListView = new OgelRegistrationListView(licenceListTab,
        referenceSortDirection,
        licenseeSortDirection,
        siteSortDirection,
        dateSortDirection,
        pageData);
    return ok(licenceList.render(licenceApplicationAddress, licenceListTab, ogelRegistrationListView));
  }

  public Result licenceDetails(String licenceRef) {
    User currentUser = userService.getCurrentUser();
    if (licenceRef != null && licenceRef.startsWith("GBSIE")) {
      return ok(licenceDetails.render(licenceApplicationAddress, licenceRef));
    } else {
      OgelDetailsView ogelDetailsView = ogelDetailsViewService.getOgelDetailsView(currentUser.getId(), licenceRef);
      return ok(ogelDetails.render(licenceApplicationAddress, ogelDetailsView));
    }
  }

}
