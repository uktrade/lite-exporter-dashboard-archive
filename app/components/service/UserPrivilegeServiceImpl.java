package components.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.common.logging.CorrelationId;
import filters.common.JwtRequestFilter;
import filters.common.JwtRequestFilterConfig;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import models.AppData;
import models.Rfi;
import org.apache.commons.lang3.ArrayUtils;
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
    Optional<UserPrivilegesView> userPrivilegeData = getPrivileges(userId);
    if (userPrivilegeData.isPresent()) {
      boolean siteAllowed = userPrivilegeData.get().getSites().stream()
          .anyMatch(siteView -> siteView.getSiteId().equals(siteId));
      boolean customerAllowed = userPrivilegeData.get().getCustomers().stream()
          .anyMatch(customerView -> customerView.getCustomerId().equals(customerId));
      return siteAllowed || customerAllowed;
    } else {
      return false;
    }
  }

  @Override
  public boolean hasRfiReplyPermission(String userId, String rfiId, AppData appData) {
    Optional<Rfi> rfi = appData.getRfiList().stream()
        .filter(rfiIterate -> rfiIterate.getId().equals(rfiId))
        .findAny();
    Optional<UserPrivilegesView> userPrivilegesView = getPrivileges(userId);
    if (rfi.isPresent() && userPrivilegesView.isPresent()) {
      boolean isRecipient = rfi.get().getRecipientUserIds().contains(userId);
      String siteId = appData.getApplication().getSiteId();
      boolean hasSiteRole = hasSiteRole(userPrivilegesView.get(), siteId, Role.ADMIN, Role.SUBMITTER);
      String customerId = appData.getApplication().getCustomerId();
      boolean hasCustomerRole = hasCustomerRole(userPrivilegesView.get(), customerId, Role.ADMIN, Role.SUBMITTER);
      return isRecipient || hasSiteRole || hasCustomerRole;
    } else {
      return false;
    }
  }

  private boolean hasCustomerRole(UserPrivilegesView userPrivilegesView, String customerId, Role... roles) {
    return userPrivilegesView.getCustomers().stream()
        .anyMatch(view -> view.getCustomerId().equals(customerId) && ArrayUtils.contains(roles, view.getRole()));
  }

  private boolean hasSiteRole(UserPrivilegesView userPrivilegesView, String siteId, Role... roles) {
    return userPrivilegesView.getSites().stream()
        .anyMatch(view -> view.getSiteId().equals(siteId) && ArrayUtils.contains(roles, view.getRole()));
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
