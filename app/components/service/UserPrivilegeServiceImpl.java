package components.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.common.logging.CorrelationId;
import filters.common.JwtRequestFilter;
import filters.common.JwtRequestFilterConfig;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import models.AppData;
import models.Rfi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
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
  private LoadingCache<String, Optional<UserPrivilegesView>> privilegesCache;
  private final JwtRequestFilter jwtRequestFilter;

  private static final Set<Role> BASIC_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER, Role.PREPARER);
  private static final Set<Role> ADVANCED_ROLES = EnumSet.of(Role.ADMIN, Role.SUBMITTER);

  @Inject
  public UserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    this.wsClient = wsClient;
    this.jwtRequestFilter = new JwtRequestFilter(spireAuthManager, new JwtRequestFilterConfig(KEY, ISSUER));
    init();
  }

  private Optional<UserPrivilegesView> getPrivileges(String userId) {
    try {
      return privilegesCache.get(userId);
    } catch (ExecutionException e) {
      LOGGER.error("ExecutionException for " + userId, e);
      return Optional.empty();
    }
  }

  @Override
  public boolean isAccessAllowed(String userId, String siteId, String customerId) {
    return hasSiteRole(userId, siteId, BASIC_ROLES) || hasCustomerRole(userId, customerId, BASIC_ROLES);
  }

  @Override
  public boolean hasRfiReplyPermission(String userId, String rfiId, AppData appData) {
    Optional<Rfi> rfi = appData.getRfiList().stream()
        .filter(rfiIterate -> rfiIterate.getId().equals(rfiId))
        .findAny();
    if (rfi.isPresent()) {
      boolean isRecipient = rfi.get().getRecipientUserIds().contains(userId);
      return isRecipient || hasBasicSiteRole(userId, appData) || hasBasicCustomerRole(userId, appData);
    } else {
      return false;
    }
  }

  @Override
  public boolean hasAmendmentOrWithdrawalPermission(String userId, AppData appData) {
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
    Optional<UserPrivilegesView> userPrivilegesView = getPrivileges(userId);
    return userPrivilegesView.isPresent() && userPrivilegesView.get().getCustomers().stream()
        .anyMatch(view -> view.getCustomerId().equals(customerId) && roles.contains(view.getRole()));
  }

  private boolean hasBasicSiteRole(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), BASIC_ROLES);
  }

  private boolean hasAdvancedSiteRole(String userId, AppData appData) {
    return hasSiteRole(userId, appData.getApplication().getSiteId(), ADVANCED_ROLES);
  }

  private boolean hasSiteRole(String userId, String siteId, Set<Role> roles) {
    Optional<UserPrivilegesView> userPrivilegesView = getPrivileges(userId);
    return userPrivilegesView.isPresent() && userPrivilegesView.get().getSites().stream()
        .anyMatch(view -> view.getSiteId().equals(siteId) && roles.contains(view.getRole()));
  }

  private void init() {
    privilegesCache = CacheBuilder.newBuilder()
        .expireAfterWrite(this.cacheExpireAfterWriteMinutes, TimeUnit.MINUTES)
        .build(new CacheLoader<String, Optional<UserPrivilegesView>>() {
                 @Override
                 public Optional<UserPrivilegesView> load(String id) throws Exception {
                   return getUserPrivilegesView(id);
                 }
               }
        );
  }

  protected Optional<UserPrivilegesView> getUserPrivilegesView(String userId) {
    WSRequest request = wsClient.url(address + USER_PRIVILEGES_PATH + userId)
        .withRequestFilter(jwtRequestFilter)
        .withRequestFilter(CorrelationId.requestFilter).setRequestTimeout(timeoutMilliseconds);
    try {
      WSResponse response = request.get().toCompletableFuture().get();
      UserPrivilegesView userPrivilegeView = Json.fromJson(response.asJson(), UserPrivilegesView.class);
      if (userPrivilegeView != null) {
        return Optional.of(userPrivilegeView);
      } else {
        LOGGER.error("userPrivilegeView is null for userId " + userId);
        return Optional.empty();
      }
    } catch (InterruptedException | ExecutionException exception) {
      String errorMessage = "Unable to get user privilegeData for user " + userId;
      LOGGER.error(errorMessage, exception);
      return Optional.empty();
    }
  }
}
