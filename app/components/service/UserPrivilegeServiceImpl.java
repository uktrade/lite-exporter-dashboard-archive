package components.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.common.logging.CorrelationId;
import components.exceptions.UnexpectedStateException;
import components.util.ApplicationUtil;
import filters.common.JwtRequestFilter;
import filters.common.JwtRequestFilterConfig;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import models.AppData;
import models.Application;
import models.Rfi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import uk.gov.bis.lite.user.api.view.CustomerView;
import uk.gov.bis.lite.user.api.view.Role;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class UserPrivilegeServiceImpl implements UserPrivilegeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPrivilegeServiceImpl.class);

  private static final String KEY = "demo-secret-which-is-very-long-so-as-to-hit-the-byte-requirement";
  private static final String ISSUER = "lite-exporter-dashboard";
  private static final String USER_PRIVILEGES_PATH = "/user-privileges/";

  private final WSClient wsClient;
  private final String address = "http://user-service.svc.dev.licensing.service.trade.gov.uk.test";
  private final int timeoutMilliseconds = 10000;
  private final int cacheExpireAfterWriteMinutes = 10;
  private LoadingCache<String, UserPrivilegesView> privilegesCache;
  private final JwtRequestFilter jwtRequestFilter;

  private static final Set<Role> BASIC_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER, Role.PREPARER);
  private static final Set<Role> ADVANCED_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER);

  @Inject
  public UserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    this.wsClient = wsClient;
    this.jwtRequestFilter = new JwtRequestFilter(spireAuthManager, new JwtRequestFilterConfig(KEY, ISSUER));
    init();
  }

  private UserPrivilegesView getPrivileges(String userId) {
    try {
      return privilegesCache.get(userId);
    } catch (ExecutionException e) {
      throw new UnexpectedStateException("ExecutionException for " + userId, e);
    }
  }

  @Override
  public boolean isAmendmentOrWithdrawalAllowed(String userId, AppData appData) {
    boolean hasAmendmentOrWithdrawalPermission = hasCreatorOrAdminPermission(userId, appData);
    boolean isApplicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean hasPendingWithdrawalRequest = ApplicationUtil.hasPendingWithdrawalRequest(appData);
    return isApplicationInProgress && !hasPendingWithdrawalRequest && hasAmendmentOrWithdrawalPermission;
  }

  @Override
  public boolean isReplyAllowed(String userId, String rfiId, AppData appData) {
    boolean hasReply = appData.getRfiReplies().stream()
        .anyMatch(rfiReply -> rfiReply.getRfiId().equals(rfiId));
    boolean wasWithdrawn = appData.getRfiWithdrawals().stream()
        .anyMatch(rfiWithdrawal -> rfiWithdrawal.getRfiId().equals(rfiId));
    boolean isApplicationInProgress = ApplicationUtil.isApplicationInProgress(appData);
    boolean hasRfiReplyPermission = hasRfiReplyPermission(userId, rfiId, appData);
    return !hasReply && !wasWithdrawn && isApplicationInProgress && hasRfiReplyPermission;
  }

  @Override
  public List<String> getCustomerIdsWithBasicPermission(String userId) {
    return getPrivileges(userId).getCustomers().stream()
        .filter(customerView -> BASIC_ROLES.contains(customerView.getRole()))
        .map(CustomerView::getCustomerId)
        .collect(Collectors.toList());
  }

  @Override
  public boolean isApplicationViewAllowed(String userId, Application application) {
    return hasSiteRole(userId, application.getSiteId(), BASIC_ROLES) || hasCustomerRole(userId, application.getCustomerId(), BASIC_ROLES);
  }

  private boolean hasRfiReplyPermission(String userId, String rfiId, AppData appData) {
    Optional<Rfi> rfi = appData.getRfiList().stream()
        .filter(rfiIterate -> rfiIterate.getId().equals(rfiId))
        .findAny();
    if (rfi.isPresent()) {
      boolean isRecipient = rfi.get().getRecipientUserIds().contains(userId);
      return isRecipient || hasAdvancedSiteRole(userId, appData) || hasAdvancedCustomerRole(userId, appData);
    } else {
      return false;
    }
  }

  @Override
  public boolean hasCreatorOrAdminPermission(String userId, AppData appData) {
    boolean isCreator = appData.getApplication().getCreatedByUserId().equals(userId);
    boolean hasCreatorPermission = isCreator && (hasBasicSiteRole(userId, appData) || hasBasicCustomerRole(userId, appData));
    boolean hasAdminPermission = hasAdvancedSiteRole(userId, appData) || hasAdvancedCustomerRole(userId, appData);
    return hasCreatorPermission || hasAdminPermission;
  }

  private boolean hasBasicCustomerRole(String userId, AppData appData) {
    return hasCustomerRole(userId, appData.getApplication().getCustomerId(), BASIC_ROLES);
  }

  private boolean hasAdvancedCustomerRole(String userId, AppData appData) {
    return hasCustomerRole(userId, appData.getApplication().getCustomerId(), ADVANCED_ROLES);
  }

  private boolean hasCustomerRole(String userId, String customerId, Set<Role> roles) {
    return getPrivileges(userId).getCustomers().stream()
        .anyMatch(view -> view.getCustomerId().equals(customerId) && roles.contains(view.getRole()));
  }

  private boolean hasBasicSiteRole(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), BASIC_ROLES);
  }

  private boolean hasAdvancedSiteRole(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), ADVANCED_ROLES);
  }

  private boolean hasSiteRole(String userId, String siteId, Set<Role> roles) {
    return getPrivileges(userId).getSites().stream()
        .anyMatch(view -> view.getSiteId().equals(siteId) && roles.contains(view.getRole()));
  }

  private void init() {
    privilegesCache = CacheBuilder.newBuilder()
        .expireAfterWrite(this.cacheExpireAfterWriteMinutes, TimeUnit.MINUTES)
        .build(new CacheLoader<String, UserPrivilegesView>() {
                 @Override
                 public UserPrivilegesView load(String id) throws Exception {
                   return getUserPrivilegesView(id);
                 }
               }
        );
  }

  protected UserPrivilegesView getUserPrivilegesView(String userId) {
    WSRequest request = wsClient.url(address + USER_PRIVILEGES_PATH + userId)
        .withRequestFilter(jwtRequestFilter)
        .withRequestFilter(CorrelationId.requestFilter).setRequestTimeout(timeoutMilliseconds);
    try {
      WSResponse response = request.get().toCompletableFuture().get();
      UserPrivilegesView userPrivilegeView = Json.fromJson(response.asJson(), UserPrivilegesView.class);
      if (userPrivilegeView != null) {
        return userPrivilegeView;
      } else {
        LOGGER.error("userPrivilegeView is null for userId " + userId);
      }
    } catch (InterruptedException | ExecutionException exception) {
      String errorMessage = "Unable to get user privilegeData for user " + userId;
      LOGGER.error(errorMessage, exception);
    }
    UserPrivilegesView userPrivilegesView = new UserPrivilegesView();
    userPrivilegesView.setCustomers(new ArrayList<>());
    userPrivilegesView.setSites(new ArrayList<>());
    return userPrivilegesView;
  }

}
