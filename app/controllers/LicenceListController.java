package controllers;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.spotify.futures.CompletableFutures;
import components.cache.SessionCache;
import components.common.client.PermissionsServiceClient;
import components.exceptions.UnknownParameterException;
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
import models.view.OgelItemView;
import models.view.SielItemView;
import play.mvc.Result;
import play.mvc.With;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;
import views.html.licenceList;
import views.html.ogelDetails;
import views.html.sielDetails;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class LicenceListController extends SamlController {

  private static final EnumSet<LicenceSortType> OGEL_SORT_TYPES = EnumSet.of(LicenceSortType.REFERENCE,
      LicenceSortType.LICENSEE,
      LicenceSortType.REGISTRATION_DATE,
      LicenceSortType.STATUS,
      LicenceSortType.LAST_UPDATED);
  private static final EnumSet<LicenceSortType> SIEL_SORT_TYPES = EnumSet.of(LicenceSortType.REFERENCE,
      LicenceSortType.LICENSEE,
      LicenceSortType.EXPIRY_DATE,
      LicenceSortType.STATUS);

  private final boolean ogelOnly;
  private final PermissionsServiceClient permissionsServiceClient;
  private final OgelItemViewService ogelItemViewService;
  private final OgelDetailsViewService ogelDetailsViewService;
  private final UserService userService;
  private final SielItemViewService sielItemViewService;
  private final SielDetailsViewService sielDetailsViewService;
  private final licenceList licenceList;
  private final ogelDetails ogelDetails;
  private final sielDetails sielDetails;

  @Inject
  public LicenceListController(@Named("ogelOnly") boolean ogelOnly,
                               PermissionsServiceClient permissionsServiceClient,
                               OgelItemViewService ogelItemViewService,
                               OgelDetailsViewService ogelDetailsViewService,
                               UserService userService,
                               SielItemViewService sielItemViewService,
                               SielDetailsViewService sielDetailsViewService,
                               licenceList licenceList,
                               ogelDetails ogelDetails,
                               sielDetails sielDetails) {
    this.ogelOnly = ogelOnly;
    this.permissionsServiceClient = permissionsServiceClient;
    this.ogelItemViewService = ogelItemViewService;
    this.ogelDetailsViewService = ogelDetailsViewService;
    this.userService = userService;
    this.sielItemViewService = sielItemViewService;
    this.sielDetailsViewService = sielDetailsViewService;
    this.licenceList = licenceList;
    this.ogelDetails = ogelDetails;
    this.sielDetails = sielDetails;
  }

  public CompletionStage<Result> licenceList(String tab, String sort, String direction, Integer page) {
    String userId = userService.getCurrentUserId();

    LicenceListState state = SessionCache.getLicenseListState(tab, sort, direction, page);
    SortDirection querySortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.ASC);
    LicenceSortType queryLicenceSortType = EnumUtil.parse(state.getSort(), LicenceSortType.class, LicenceSortType.STATUS);
    LicenceListTab queryLicenceListTab = EnumUtil.parse(state.getTab(), LicenceListTab.class, LicenceListTab.SIELS);

    CompletionStage<List<OgelRegistrationView>> ogelStage = permissionsServiceClient.getOgelRegistrations(userId);
    CompletionStage<List<LicenceView>> licenceStage;
    if (ogelOnly) {
      licenceStage = CompletableFuture.completedFuture(new ArrayList<>());
    } else {
      licenceStage = permissionsServiceClient.getLicences(userId);
    }

    return CompletableFutures.combineFutures(ogelStage, licenceStage, (ogelRegistrationViews, licenceViews) -> {

      boolean hasOgels = !ogelRegistrationViews.isEmpty();
      boolean hasSiels = !licenceViews.isEmpty();

      LicenceListTab licenceListTab = createLicenceListTab(queryLicenceListTab, hasSiels, hasOgels);

      boolean isCoherent = isTabAllowed(licenceListTab, queryLicenceSortType);
      LicenceSortType licenceSortType = isCoherent ? queryLicenceSortType : LicenceSortType.REFERENCE;
      SortDirection sortDirection = isCoherent ? querySortDirection : SortDirection.ASC;

      if (licenceListTab == LicenceListTab.OGELS) {
        return ogelItemViewService.getOgelItemViews(ogelRegistrationViews).thenApply(ogelItemViews -> {
          SortUtil.sortOgels(ogelItemViews, licenceSortType, sortDirection);
          Page<OgelItemView> ogelPage = PageUtil.getPage(page, ogelItemViews);
          int currentPage = ogelPage.getCurrentPage();
          LicenceListView licenceListView = new LicenceListView(hasSiels, hasOgels, licenceListTab, licenceSortType, sortDirection, ogelPage, null, currentPage);
          return ok(licenceList.render(licenceListView));
        });
      } else {
        return sielItemViewService.getSielItemViews(licenceViews).thenApply(sielItemViews -> {
          SortUtil.sortSiels(sielItemViews, licenceSortType, sortDirection);
          Page<SielItemView> sielPage = PageUtil.getPage(page, sielItemViews);
          int currentPage = sielPage.getCurrentPage();
          LicenceListView licenceListView = new LicenceListView(hasSiels, hasOgels, licenceListTab, licenceSortType, sortDirection, null, sielPage, currentPage);
          return ok(licenceList.render(licenceListView));
        });
      }
    });
  }

  private LicenceListTab createLicenceListTab(LicenceListTab queryLicenceListTab, boolean hasSiels, boolean hasOgels) {
    if (queryLicenceListTab == LicenceListTab.SIELS && !hasSiels && hasOgels) {
      return LicenceListTab.OGELS;
    } else if (queryLicenceListTab == LicenceListTab.OGELS && !hasOgels && hasSiels) {
      return LicenceListTab.SIELS;
    } else {
      return queryLicenceListTab;
    }
  }

  private boolean isTabAllowed(LicenceListTab licenceListTab, LicenceSortType licenceSortType) {
    return (licenceListTab == LicenceListTab.OGELS && OGEL_SORT_TYPES.contains(licenceSortType)) ||
        (licenceListTab == LicenceListTab.SIELS && SIEL_SORT_TYPES.contains(licenceSortType));
  }

  public CompletionStage<Result> ogelDetails(String registrationReference) {
    String userId = userService.getCurrentUserId();
    return permissionsServiceClient.getOgelRegistration(userId, registrationReference).exceptionally(error -> {
      throw UnknownParameterException.unknownOgelId(registrationReference);
    }).thenCompose(ogelRegistrationView -> ogelDetailsViewService.getOgelDetailsView(ogelRegistrationView)
        .thenApply(ogelDetailsView -> ok(ogelDetails.render(ogelDetailsView))));
  }

  @With(OgelOnlyGuardAction.class)
  public CompletionStage<Result> sielDetails(String registrationReference) {
    String userId = userService.getCurrentUserId();
    return permissionsServiceClient.getLicence(userId, registrationReference).exceptionally(error -> {
      throw UnknownParameterException.unknownSielId(registrationReference);
    }).thenCompose(licenceView -> sielDetailsViewService.getSielDetailsView(licenceView)
        .thenApply(sielDetailsView -> ok(sielDetails.render(sielDetailsView))));
  }

}
