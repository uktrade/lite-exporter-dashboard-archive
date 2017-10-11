package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.cache.SessionCache;
import components.service.ApplicationItemViewService;
import components.service.UserService;
import components.util.Comparators;
import components.util.EnumUtil;
import components.util.PageUtil;
import components.util.SortUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import models.ApplicationListState;
import models.Page;
import models.enums.ApplicationListTab;
import models.enums.ApplicationProgress;
import models.enums.ApplicationSortType;
import models.enums.SortDirection;
import models.view.ApplicationItemView;
import models.view.ApplicationListView;
import models.view.CompanySelectItemView;
import play.mvc.Result;
import views.html.applicationList;

public class ApplicationListController extends SamlController {

  private static final Set<ApplicationSortType> USER_SORT_TYPES = EnumSet.of(ApplicationSortType.DATE, ApplicationSortType.REFERENCE, ApplicationSortType.STATUS, ApplicationSortType.DESTINATION);
  private static final Set<ApplicationSortType> COMPANY_SORT_TYPES = EnumSet.of(ApplicationSortType.DATE, ApplicationSortType.REFERENCE, ApplicationSortType.STATUS, ApplicationSortType.DESTINATION, ApplicationSortType.CREATED_BY);
  private static final Set<ApplicationSortType> ATTENTION_SORT_TYPES = EnumSet.of(ApplicationSortType.REFERENCE, ApplicationSortType.CREATED_BY, ApplicationSortType.EVENT_TYPE, ApplicationSortType.EVENT_DATE);

  private final String licenceApplicationAddress;
  private final ApplicationItemViewService applicationItemViewService;
  private final UserService userService;

  @Inject
  public ApplicationListController(@Named("licenceApplicationAddress") String licenceApplicationAddress,
                                   ApplicationItemViewService applicationItemViewService,
                                   UserService userService) {
    this.licenceApplicationAddress = licenceApplicationAddress;
    this.applicationItemViewService = applicationItemViewService;
    this.userService = userService;
  }

  public Result index() {
    return redirect("/applications");
  }

  public Result applicationList(String tab, String sort, String direction, String company, String show, Integer page) {
    String userId = userService.getCurrentUserId();

    ApplicationListState state = SessionCache.getApplicationListState(tab, sort, direction, company, show, page);
    ApplicationProgress applicationProgress = EnumUtil.parse(state.getShow(), ApplicationProgress.class);
    ApplicationSortType applicationSortType = EnumUtil.parse(state.getSort(), ApplicationSortType.class, ApplicationSortType.DATE);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.DESC);
    ApplicationListTab applicationListTab = EnumUtil.parse(state.getTab(), ApplicationListTab.class, ApplicationListTab.USER);
    String companyId = state.getCompany();

    List<ApplicationItemView> views = applicationItemViewService.getApplicationItemViews(userId);

    boolean hasUserApplications = hasUserApplications(userId, views);
    boolean hasOtherUserApplications = hasOtherUserApplications(userId, views);
    boolean hasForYourAttentionApplications = hasForYourAttentionApplications(views);

    applicationListTab = defaultTab(applicationListTab, hasUserApplications, hasOtherUserApplications, hasForYourAttentionApplications);

    if ((applicationListTab == ApplicationListTab.USER && !USER_SORT_TYPES.contains(applicationSortType)) ||
        applicationListTab == ApplicationListTab.COMPANY && !COMPANY_SORT_TYPES.contains(applicationSortType)) {
      applicationSortType = ApplicationSortType.DATE;
      sortDirection = SortDirection.DESC;
    } else if (applicationListTab == ApplicationListTab.ATTENTION && !ATTENTION_SORT_TYPES.contains(applicationSortType)) {
      applicationSortType = ApplicationSortType.EVENT_DATE;
      sortDirection = SortDirection.DESC;
    }

    if (applicationListTab == ApplicationListTab.ATTENTION && applicationSortType == ApplicationSortType.CREATED_BY && hasUserApplications && !hasOtherUserApplications) {
      applicationSortType = ApplicationSortType.EVENT_DATE;
      sortDirection = SortDirection.DESC;
    }

    if (applicationListTab == ApplicationListTab.ATTENTION) {
      companyId = "all";
      applicationProgress = null;
    }

    List<CompanySelectItemView> companyNames = collectCompanyNames(views);

    List<ApplicationItemView> companyFilteredViews = filterByCompanyId(companyId, views);
    List<ApplicationItemView> userFilteredViews = filterByUser(userId, applicationListTab, companyFilteredViews);
    List<ApplicationItemView> applicationProgressFilteredViews = filterByApplicationProgress(applicationProgress, userFilteredViews);
    List<ApplicationItemView> attentionFilteredViews = filterByAttention(applicationListTab, applicationProgressFilteredViews);

    SortUtil.sort(attentionFilteredViews, applicationSortType, sortDirection);

    Page<ApplicationItemView> pageData = PageUtil.getPage(state.getPage(), attentionFilteredViews);

    long allCount = userFilteredViews.size();
    long draftCount = countByApplicationProgress(userFilteredViews, ApplicationProgress.DRAFT);
    long completedCount = countByApplicationProgress(userFilteredViews, ApplicationProgress.COMPLETED);
    long currentCount = allCount - draftCount - completedCount;

    ApplicationListView applicationListView = new ApplicationListView(applicationListTab,
        companyId,
        companyNames,
        hasUserApplications,
        hasOtherUserApplications,
        hasForYourAttentionApplications,
        applicationSortType,
        sortDirection,
        applicationProgress,
        allCount,
        draftCount,
        currentCount,
        completedCount,
        pageData);

    return ok(applicationList.render(licenceApplicationAddress, applicationListView)).withHeader("Cache-Control", "no-store");
  }

  private ApplicationListTab defaultTab(ApplicationListTab applicationListTab, boolean hasUserApplications, boolean hasOtherUserApplications, boolean hasForYourAttentionApplications) {
    if (applicationListTab == ApplicationListTab.COMPANY && hasUserApplications && !hasOtherUserApplications) {
      return ApplicationListTab.USER;
    } else if (applicationListTab == ApplicationListTab.USER && !hasUserApplications && hasOtherUserApplications) {
      return ApplicationListTab.COMPANY;
    } else if (applicationListTab == ApplicationListTab.ATTENTION && !hasForYourAttentionApplications) {
      if (hasUserApplications) {
        return ApplicationListTab.USER;
      } else if (hasOtherUserApplications) {
        return ApplicationListTab.COMPANY;
      }
    }
    return applicationListTab;
  }

  private List<CompanySelectItemView> collectCompanyNames(List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(view -> new CompanySelectItemView(view.getCompanyId(), view.getCompanyName()))
        .sorted(Comparators.COMPANY_NAME)
        .collect(Collectors.toList());
  }

  private boolean hasUserApplications(String currentUserId, List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .map(ApplicationItemView::getCreatedById)
        .anyMatch(currentUserId::equals);
  }

  private boolean hasOtherUserApplications(String currentUserId, List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .map(ApplicationItemView::getCreatedById)
        .anyMatch(id -> !currentUserId.equals(id));
  }

  private boolean hasForYourAttentionApplications(List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .map(ApplicationItemView::getForYourAttentionNotificationView)
        .anyMatch(Objects::nonNull);
  }

  private long countByApplicationProgress(List<ApplicationItemView> applicationItemViews, ApplicationProgress applicationProgress) {
    return applicationItemViews.stream()
        .filter(view -> view.getApplicationProgress() == applicationProgress)
        .count();
  }

  private List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.USER) {
      return applicationItemViews.stream()
          .filter(view -> userId.equals(view.getCreatedById()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByCompanyId(String companyId, List<ApplicationItemView> applicationItemViews) {
    if (companyId != null && !companyId.equals("all")) {
      return applicationItemViews.stream()
          .filter(view -> companyId.equals(view.getCompanyId()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByApplicationProgress(ApplicationProgress applicationProgress, List<ApplicationItemView> applicationItemViews) {
    if (applicationProgress != null) {
      return applicationItemViews.stream()
          .filter(view -> view.getApplicationProgress() == applicationProgress)
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByAttention(ApplicationListTab applicationListTab, List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.ATTENTION) {
      return applicationItemViews.stream()
          .filter(view -> view.getForYourAttentionNotificationView() != null)
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

}
