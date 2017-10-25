package components.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.Inject;
import components.common.auth.SpireAuthManager;
import components.common.logging.CorrelationId;
import filters.common.JwtRequestFilter;
import filters.common.JwtRequestFilterConfig;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import models.UserPrivilegeData;
import models.UserPrivilegeView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public class UserPrivilegeServiceImpl implements UserPrivilegeService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPrivilegeServiceImpl.class);
  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  private static final String KEY = "demo-secret-which-is-very-long-so-as-to-hit-the-byte-requirement";
  private static final String ISSUER = "lite-exporter-dashboard";
  private static final String USER_PRIVILEGES_PATH = "/user-privileges/";

  private final WSClient wsClient;
  private final String address = "http://user-service.svc.dev.licensing.service.trade.gov.uk.test";
  private final int timeoutMilliseconds = 10000;
  private final int cacheExpireAfterWriteMinutes = 10;
  private LoadingCache<String, Optional<UserPrivilegeData>> privilegesCache;
  private final JwtRequestFilter jwtRequestFilter;

  @Inject
  public UserPrivilegeServiceImpl(WSClient wsClient, SpireAuthManager spireAuthManager) {
    this.wsClient = wsClient;
    this.jwtRequestFilter = new JwtRequestFilter(spireAuthManager, new JwtRequestFilterConfig(KEY, ISSUER));
    init();
  }

  private Optional<UserPrivilegeData> getPrivileges(String userId) {
    try {
      return privilegesCache.get(userId);
    } catch (ExecutionException e) {
      LOGGER.error("ExecutionException for: " + userId, e);
      return Optional.empty();
    }
  }

  @Override
  public boolean isAccessAllowed(String userId, String customerId) {
    Optional<UserPrivilegeData> userPrivilegeData = getPrivileges(userId);
    return userPrivilegeData.isPresent() && userPrivilegeData.get().getCustomerIds().contains(customerId);
  }

  private void init() {
    privilegesCache = CacheBuilder.newBuilder()
        .expireAfterWrite(this.cacheExpireAfterWriteMinutes, TimeUnit.MINUTES)
        .build(
            new CacheLoader<String, Optional<UserPrivilegeData>>() {
              @Override
              public Optional<UserPrivilegeData> load(String id) throws Exception {
                return getUserPrivilegeData(id);
              }
            }
        );
  }

  private Optional<UserPrivilegeData> getUserPrivilegeData(String userId) {
    WSRequest request = wsClient.url(address + USER_PRIVILEGES_PATH + userId)
        .withRequestFilter(jwtRequestFilter)
        .withRequestFilter(CorrelationId.requestFilter).setRequestTimeout(timeoutMilliseconds);
    try {
      WSResponse response = request.get().toCompletableFuture().get();
      UserPrivilegeView userPrivilegeView = Json.fromJson(response.asJson(), UserPrivilegeView.class);
      if (userPrivilegeView != null) {
        Set<String> customerIds = userPrivilegeView.getCustomers().stream()
            .map(CustomerView::getCustomerId)
            .collect(Collectors.toSet());
        Set<String> siteIds = userPrivilegeView.getSites().stream()
            .map(SiteView::getSiteId)
            .collect(Collectors.toSet());
        return Optional.of(new UserPrivilegeData(customerIds, siteIds));
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
