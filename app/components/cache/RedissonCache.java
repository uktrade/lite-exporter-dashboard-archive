package components.cache;

import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import play.Logger;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class RedissonCache {

  private final RedissonClient redissonClient;
  private final String prefix;
  private final String key;

  @Inject
  public RedissonCache(RedissonClient redissonClient, String prefix, String key) {
    this.redissonClient = redissonClient;
    this.prefix = prefix;
    this.key = key;
  }

  public <T> T get(Supplier<T> supplier, String method, String... arguments) {
    String hashKey = hashKey(method, arguments);
    RBucket<T> rBucket = redissonClient.getBucket(hashKey);
    T cachedObject = rBucket.get();
    if (cachedObject != null) {
      Logger.error("returned cached object {}", hashKey);
      return cachedObject;
    } else {
      try {
        T object = supplier.get();
        rBucket.set(object, 30, TimeUnit.SECONDS);
        return object;
      } catch (Exception exception) {
        Logger.error("Unable to get object {}", hashKey, exception);
        return null;
      }
    }
  }

  private String hashKey(String method, String... arguments) {
    return prefix + ":" + key + ":" + method + ":" + StringUtils.join(arguments, ":");
  }

}
