package components.cache;

import com.google.inject.Inject;
import components.client.LicenceClient;
import org.redisson.api.RedissonClient;
import uk.gov.bis.lite.permissions.api.view.LicenceView;
import uk.gov.bis.lite.permissions.api.view.OgelRegistrationView;

import java.util.List;

public class LicenceClientCacheImpl implements LicenceClientCache {

  private final RedissonCache redissonCache;
  private final LicenceClient licenceClient;

  @Inject
  public LicenceClientCacheImpl(RedissonClient redissonClient, LicenceClient licenceClient) {
    this.redissonCache = new RedissonCache(redissonClient, "dashboard", "licenceClient");
    this.licenceClient = licenceClient;
  }

  @Override
  public LicenceView getLicence(String userId, String reference) {
    return redissonCache.get(() -> licenceClient.getLicence(userId, reference), "getLicence", userId, reference);
  }

  @Override
  public List<LicenceView> getLicences(String userId) {
    return redissonCache.get(() -> licenceClient.getLicences(userId), "getLicences", userId);
  }

  @Override
  public OgelRegistrationView getOgelRegistration(String userId, String reference) {
    return redissonCache.get(() -> licenceClient.getOgelRegistration(userId, reference), "getOgelRegistration", userId, reference);
  }

  @Override
  public List<OgelRegistrationView> getOgelRegistrations(String userId) {
    return redissonCache.get(() -> licenceClient.getOgelRegistrations(userId), "getOgelRegistrations", userId);
  }

}
