package components.service;

import com.google.inject.Inject;
import components.cache.UserServiceClientCache;
import components.util.ApplicationUtil;
import models.AppData;
import models.Application;
import models.CaseData;
import models.Rfi;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;
import uk.gov.bis.lite.user.api.view.enums.Role;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserPermissionServiceImpl implements UserPermissionService {

  private final UserServiceClientCache userServiceClientCache;

  private static final Set<Role> VIEWER_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER, Role.PREPARER);
  private static final Set<Role> ADMIN_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER);

  @Inject
  public UserPermissionServiceImpl(UserServiceClientCache userServiceClientCache) {
    this.userServiceClientCache = userServiceClientCache;
  }

  @Override
  public boolean canAddAmendmentOrWithdrawalRequest(String userId, AppData appData) {
    boolean hasCreatorOrAdminPermission = hasCreatorOrAdminPermission(userId, appData);
    boolean isOriginalApplicationInProgress = ApplicationUtil.isOriginalApplicationInProgress(appData);
    boolean hasPendingWithdrawalRequest = ApplicationUtil.hasPendingWithdrawalRequest(appData);
    return isOriginalApplicationInProgress && !hasPendingWithdrawalRequest && hasCreatorOrAdminPermission;
  }

  @Override
  public boolean canAddRfiReply(String userId, String rfiId, AppData appData) {
    boolean hasReply = ApplicationUtil.getAllRfiReplies(appData).stream()
        .anyMatch(rfiReply -> rfiReply.getRfiId().equals(rfiId));
    boolean wasWithdrawn = ApplicationUtil.getAllRfiWithdrawals(appData).stream()
        .anyMatch(rfiWithdrawal -> rfiWithdrawal.getRfiId().equals(rfiId));

    boolean isApplicationInProgress;
    Optional<CaseData> caseDataOptional = ApplicationUtil.getMostRecentCase(appData);
    if (caseDataOptional.isPresent()) {
      CaseData mostRecentCase = caseDataOptional.get();
      isApplicationInProgress = ApplicationUtil.isCaseInProgress(mostRecentCase) && ApplicationUtil.getRfi(mostRecentCase.getRfiList(), rfiId).isPresent();
    } else {
      isApplicationInProgress = ApplicationUtil.isOriginalApplicationInProgress(appData) && ApplicationUtil.getRfi(appData.getRfiList(), rfiId).isPresent();
    }

    boolean hasRfiReplyPermission = hasRfiReplyPermission(userId, rfiId, appData);
    return !hasReply && !wasWithdrawn && isApplicationInProgress && hasRfiReplyPermission;
  }

  @Override
  public List<String> getCustomerIdsWithViewingPermission(String userId) {
    return getPrivileges(userId).getCustomers().stream()
        .filter(customerView -> VIEWER_ROLES.contains(customerView.getRole()))
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
  }

  @Override
  public boolean canViewApplication(String userId, Application application) {
    return hasSiteRole(userId, application.getSiteId(), VIEWER_ROLES) || hasCustomerRole(userId, application.getCustomerId(), VIEWER_ROLES);
  }

  @Override
  public boolean hasCreatorOrAdminPermission(String userId, AppData appData) {
    boolean isCreator = appData.getApplication().getCreatedByUserId().equals(userId);
    boolean hasCreatorPermission = isCreator && (canViewSite(userId, appData) || canViewCustomer(userId, appData));
    boolean hasAdminPermission = canAdminSite(userId, appData) || canAdminCustomer(userId, appData);
    return hasCreatorPermission || hasAdminPermission;
  }

  private boolean hasRfiReplyPermission(String userId, String rfiId, AppData appData) {
    Optional<Rfi> rfi = ApplicationUtil.getRfi(ApplicationUtil.getAllRfi(appData), rfiId);
    if (rfi.isPresent()) {
      boolean isRecipient = rfi.get().getRecipientUserIds().contains(userId);
      return isRecipient || canAdminSite(userId, appData) || canAdminCustomer(userId, appData);
    } else {
      return false;
    }
  }

  private boolean canViewCustomer(String userId, AppData appData) {
    return hasCustomerRole(userId, appData.getApplication().getCustomerId(), VIEWER_ROLES);
  }

  private boolean canAdminCustomer(String userId, AppData appData) {
    return hasCustomerRole(userId, appData.getApplication().getCustomerId(), ADMIN_ROLES);
  }

  private boolean hasCustomerRole(String userId, String customerId, Set<Role> roles) {
    return customerId != null && getPrivileges(userId).getCustomers().stream()
        .anyMatch(view -> view.getCustomerId().equals(customerId) && roles.contains(view.getRole()));
  }

  private boolean canViewSite(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), VIEWER_ROLES);
  }

  private boolean canAdminSite(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), ADMIN_ROLES);
  }

  private boolean hasSiteRole(String userId, String siteId, Set<Role> roles) {
    return siteId != null && getPrivileges(userId).getSites().stream()
        .anyMatch(view -> view.getSiteId().equals(siteId) && roles.contains(view.getRole()));
  }

  private UserPrivilegesView getPrivileges(String userId) {
    UserPrivilegesView userPrivilegesView = userServiceClientCache.getUserPrivilegeView(userId);
    if (userPrivilegesView != null) {
      return userPrivilegesView;
    } else {
      UserPrivilegesView view = new UserPrivilegesView();
      view.setCustomers(new ArrayList<>());
      view.setSites(new ArrayList<>());
      return view;
    }
  }

}
