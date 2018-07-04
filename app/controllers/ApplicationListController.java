package controllers;

import static components.util.StreamUtil.distinctByKey;

import com.google.inject.Inject;
import components.cache.SessionCache;
import components.service.ApplicationItemViewService;
import components.service.UserService;
import components.util.Comparators;
import components.util.EnumUtil;
import components.util.PageUtil;
import components.util.SortUtil;
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
import play.mvc.With;
import views.html.applicationList;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@With(OgelOnlyGuardAction.class)
public class ApplicationListController extends SamlController {

  private static final String COMPANY_ID_ALL = "all";

  private static final Set<ApplicationSortType> USER_SORT_TYPES = EnumSet.of(ApplicationSortType.DATE, ApplicationSortType.REFERENCE, ApplicationSortType.STATUS, ApplicationSortType.DESTINATION);
  private static final Set<ApplicationSortType> COMPANY_SORT_TYPES = EnumSet.of(ApplicationSortType.DATE, ApplicationSortType.REFERENCE, ApplicationSortType.STATUS, ApplicationSortType.DESTINATION, ApplicationSortType.CREATED_BY);
  private static final Set<ApplicationSortType> ATTENTION_SORT_TYPES = EnumSet.of(ApplicationSortType.REFERENCE, ApplicationSortType.CREATED_BY, ApplicationSortType.EVENT_TYPE, ApplicationSortType.EVENT_DATE);

  private final ApplicationItemViewService applicationItemViewService;
  private final UserService userService;
  private final applicationList applicationList;

  @Inject
  public ApplicationListController(ApplicationItemViewService applicationItemViewService, UserService userService,
                                   applicationList applicationList) {
    this.applicationItemViewService = applicationItemViewService;
    this.userService = userService;
    this.applicationList = applicationList;
  }

  public Result applicationList(String tab, String sort, String direction, String company, String show, Integer page) {
    String userId = userService.getCurrentUserId();

    ApplicationListState state = SessionCache.getApplicationListState(tab, sort, direction, company, show, page);
    ApplicationProgress applicationProgress = EnumUtil.parse(state.getShow(), ApplicationProgress.class);
    ApplicationSortType applicationSortType = EnumUtil.parse(state.getSort(), ApplicationSortType.class, ApplicationSortType.DATE);
    SortDirection sortDirection = EnumUtil.parse(state.getDirection(), SortDirection.class, SortDirection.DESC);
    ApplicationListTab applicationListTab = EnumUtil.parse(state.getTab(), ApplicationListTab.class, ApplicationListTab.USER);
    String companyIdParam = state.getCompany();

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
      applicationProgress = null;
    }

    boolean hasApplicationWithNoCompanyId = views.stream()
        .anyMatch(view -> view.getCompanyId() == null);

    List<CompanySelectItemView> companySelectItemViews = collectCompanyNames(views);

    String companyId = defaultCompanyId(applicationListTab, companyIdParam, companySelectItemViews);

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
        companySelectItemViews,
        hasApplicationWithNoCompanyId,
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

    return ok(applicationList.render(applicationListView)).withHeader("Cache-Control", "no-store, no-cache");
  }

  private String defaultCompanyId(ApplicationListTab applicationListTab, String companyId,
                                  List<CompanySelectItemView> companySelectItemViews) {
    if (applicationListTab == ApplicationListTab.ATTENTION || companyId == null || COMPANY_ID_ALL.equals(companyId) ||
        companySelectItemViews.stream().noneMatch(companySelectItemView -> companySelectItemView.getCompanyId().equals(companyId))) {
      return COMPANY_ID_ALL;
    } else {
      return companyId;
    }
  }

  private ApplicationListTab defaultTab(ApplicationListTab applicationListTab, boolean hasUserApplications,
                                        boolean hasOtherUserApplications, boolean hasForYourAttentionApplications) {
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
        .filter(view -> view.getCompanyId() != null)
        .filter(distinctByKey(ApplicationItemView::getCompanyId))
        .map(view -> new CompanySelectItemView(view.getCompanyId(), view.getCompanyName()))
        .sorted(Comparators.COMPANY_NAME)
        .collect(Collectors.toList());
  }

  private boolean hasUserApplications(String currentUserId, List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .anyMatch(applicationItemView -> currentUserId.equals(applicationItemView.getCreatedById()));
  }

  private boolean hasOtherUserApplications(String currentUserId, List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .anyMatch(applicationItemView -> !currentUserId.equals(applicationItemView.getCreatedById()));
  }

  private boolean hasForYourAttentionApplications(List<ApplicationItemView> applicationItemViews) {
    return applicationItemViews.stream()
        .anyMatch(applicationItemView -> !applicationItemView.getForYourAttentionNotificationViews().isEmpty());
  }

  private long countByApplicationProgress(List<ApplicationItemView> applicationItemViews,
                                          ApplicationProgress applicationProgress) {
    return applicationItemViews.stream()
        .filter(view -> view.getApplicationProgress() == applicationProgress)
        .count();
  }

  private List<ApplicationItemView> filterByUser(String userId, ApplicationListTab applicationListTab,
                                                 List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.USER) {
      return applicationItemViews.stream()
          .filter(view -> userId.equals(view.getCreatedById()))
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByCompanyId(String companyId,
                                                      List<ApplicationItemView> applicationItemViews) {
    if (COMPANY_ID_ALL.equals(companyId)) {
      return applicationItemViews;
    } else {
      return applicationItemViews.stream()
          .filter(view -> companyId.equals(view.getCompanyId()))
          .collect(Collectors.toList());
    }
  }

  private List<ApplicationItemView> filterByApplicationProgress(ApplicationProgress applicationProgress,
                                                                List<ApplicationItemView> applicationItemViews) {
    if (applicationProgress != null) {
      return applicationItemViews.stream()
          .filter(view -> view.getApplicationProgress() == applicationProgress)
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

  private List<ApplicationItemView> filterByAttention(ApplicationListTab applicationListTab,
                                                      List<ApplicationItemView> applicationItemViews) {
    if (applicationListTab == ApplicationListTab.ATTENTION) {
      return applicationItemViews.stream()
          .filter(view -> !view.getForYourAttentionNotificationViews().isEmpty())
          .collect(Collectors.toList());
    } else {
      return applicationItemViews;
    }
  }

}
