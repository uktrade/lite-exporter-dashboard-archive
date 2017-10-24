package components.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import models.UserPrivilegeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.libs.Json;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

public class UserPrivilegeServiceImpl implements UserPrivilegeService {

  private static final ObjectWriter WRITER = new ObjectMapper().writerWithDefaultPrettyPrinter();

  private static final Logger LOGGER = LoggerFactory.getLogger(UserPrivilegeServiceImpl.class);
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

  @Override
  public void get(String userId) {
    try {
      Optional<UserPrivilegeData> opt = privilegesCache.get(userId);
      if (opt.isPresent()) {
        try {
          LOGGER.error(WRITER.writeValueAsString(opt.get()));
        } catch (JsonProcessingException e) {
          e.printStackTrace();
        }
      }
    } catch (ExecutionException e) {
      LOGGER.error(userId, e);
    }
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
      return Optional.of(Json.fromJson(response.asJson(), UserPrivilegeData.class));
    } catch (InterruptedException | ExecutionException exception) {
      String errorMessage = "Unable to get user privilegeData for user " + userId;
      LOGGER.error(errorMessage, exception);
    }
    return Optional.empty();
  }
}
