package components.cache;

import com.google.inject.Inject;
import components.client.UserServiceClient;
import org.redisson.api.RedissonClient;
import uk.gov.bis.lite.user.api.view.UserPrivilegesView;

public class UserServiceClientCacheImpl implements UserServiceClientCache {

  private final RedissonCache redissonCache;
  private final UserServiceClient userServiceClient;

  @Inject
  public UserServiceClientCacheImpl(RedissonClient redissonClient, UserServiceClient userServiceClient) {
    this.redissonCache = new RedissonCache(redissonClient, "dashboard", "userServiceClient");
    this.userServiceClient = userServiceClient;
  }

  @Override
  public UserPrivilegesView getUserPrivilegeView(String userId) {
    return redissonCache.get(() -> userServiceClient.getUserPrivilegeView(userId), "getUserPrivilegeView", userId);
  }
}
