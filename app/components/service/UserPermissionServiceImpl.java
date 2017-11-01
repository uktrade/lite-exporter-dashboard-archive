package components.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import components.client.OgelRegistrationServiceClient;
import components.client.UserServiceClient;
import components.dao.SielDao;
import components.exceptions.UnexpectedStateException;
import components.util.ApplicationUtil;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import models.AppData;
import models.Application;
import models.Rfi;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class UserPermissionServiceImpl implements UserPermissionService {

  private final UserServiceClient userServiceClient;
  private final LoadingCache<String, UserPrivilegesView> privilegesCache;
  private final OgelRegistrationServiceClient ogelRegistrationServiceClient;
  private final SielDao sielDao;


  private static final Set<Role> VIEWER_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER, Role.PREPARER);
  private static final Set<Role> ADMIN_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER);

  @Inject
  public UserPermissionServiceImpl(@Named("userServiceCacheExpireMinutes") Long cacheExpireMinutes,
                                   UserServiceClient userServiceClient,
                                   OgelRegistrationServiceClient ogelRegistrationServiceClient,
                                   SielDao sielDao) {
    this.userServiceClient = userServiceClient;
    this.privilegesCache = createPrivilegesCache(cacheExpireMinutes);
    this.ogelRegistrationServiceClient = ogelRegistrationServiceClient;
    this.sielDao = sielDao;
  }

  private LoadingCache<String, UserPrivilegesView> createPrivilegesCache(Long cacheExpireMinutes) {
    return CacheBuilder.newBuilder()
        .expireAfterWrite(cacheExpireMinutes, TimeUnit.MINUTES)
        .build(new CacheLoader<String, UserPrivilegesView>() {
                 @Override
                 public UserPrivilegesView load(@Nonnull String id) throws Exception {
                   return getUserPrivilegesView(id);
                 }
               }
        );
  }

  private UserPrivilegesView getPrivileges(String userId) {
    try {
      return privilegesCache.get(userId);
    } catch (ExecutionException exception) {
      String message = "Unable to get user privileges from cache for user id " + userId;
      throw new UnexpectedStateException(message, exception);
    }
  }

  @Override
  public boolean canViewOgel(String userId, String registrationReference) {
    List<OgelRegistrationView> ogelRegistrationViews = ogelRegistrationServiceClient.getOgelRegistrations(userId);
    return ogelRegistrationViews.stream()
        .anyMatch(ogelRegistrationView -> ogelRegistrationView.getRegistrationReference().equals(registrationReference));
  }

  @Override
  public boolean canViewSiel(String userId, String registrationReference) {
    List<String> customerIds = getCustomerIdsWithViewingPermission(userId);
    return sielDao.getSiels(customerIds).stream()
        .anyMatch(siel -> siel.getCaseReference().equals(registrationReference));
  }

  @Override
  public boolean canAddAmendmentOrWithdrawalRequest(String userId, AppData appData) {
    boolean hasCreatorOrAdminPermission = hasCreatorOrAdminPermission(userId, appData);
    boolean isApplicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean hasPendingWithdrawalRequest = ApplicationUtil.hasPendingWithdrawalRequest(appData);
    return isApplicationInProgress && !hasPendingWithdrawalRequest && hasCreatorOrAdminPermission;
  }

  @Override
  public boolean canAddRfiReply(String userId, String rfiId, AppData appData) {
    boolean hasReply = appData.getRfiReplies().stream()
        .anyMatch(rfiReply -> rfiReply.getRfiId().equals(rfiId));
    boolean wasWithdrawn = appData.getRfiWithdrawals().stream()
        .anyMatch(rfiWithdrawal -> rfiWithdrawal.getRfiId().equals(rfiId));
    boolean isApplicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
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
    Optional<Rfi> rfi = appData.getRfiList().stream()
        .filter(rfiIterate -> rfiIterate.getId().equals(rfiId))
        .findAny();
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
    return getPrivileges(userId).getCustomers().stream()
        .anyMatch(view -> view.getCustomerId().equals(customerId) && roles.contains(view.getRole()));
  }

  private boolean canViewSite(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), VIEWER_ROLES);
  }

  private boolean canAdminSite(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), ADMIN_ROLES);
  }

  private boolean hasSiteRole(String userId, String siteId, Set<Role> roles) {
    return getPrivileges(userId).getSites().stream()
        .anyMatch(view -> view.getSiteId().equals(siteId) && roles.contains(view.getRole()));
  }

  protected UserPrivilegesView getUserPrivilegesView(String userId) {
    Optional<UserPrivilegesView> userPrivilegesViewOptional = userServiceClient.getUserPrivilegeView(userId);
    if (userPrivilegesViewOptional.isPresent()) {
      return userPrivilegesViewOptional.get();
    } else {
      UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
      userPrivilegesView.setCustomers(new ArrayList<>());
      userPrivilegesView.setSites(new ArrayList<>());
      return userPrivilegesView;
    }
  }

}
