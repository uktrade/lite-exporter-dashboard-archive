package components.cache;

import com.google.inject.Inject;
import components.client.OgelServiceClient;
import org.redisson.api.RedissonClient;
import uk.gov.bis.lite.ogel.api.view.OgelFullView;

public class OgelServiceClientCacheImpl implements OgelServiceClientCache {

  private final OgelServiceClient ogelServiceClient;
  private final RedissonCache redissonCache;

  @Inject
  public OgelServiceClientCacheImpl(RedissonClient redissonClient, OgelServiceClient ogelServiceClient) {
    this.redissonCache = new RedissonCache(redissonClient, "dashboard", "ogelServiceClient");
    this.ogelServiceClient = ogelServiceClient;
  }

  @Override
  public OgelFullView getOgel(String ogelId) {
    return redissonCache.get(() -> ogelServiceClient.getOgel(ogelId), "getOgel", ogelId);
  }


}
