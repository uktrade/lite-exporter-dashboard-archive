package components.cache;

import com.google.inject.Inject;
import components.client.CustomerServiceClient;
import org.redisson.api.RedissonClient;
import uk.gov.bis.lite.customer.api.view.CustomerView;
import uk.gov.bis.lite.customer.api.view.SiteView;

public class CustomerServiceClientCacheImpl implements CustomerServiceClientCache {

  private final CustomerServiceClient customerServiceClient;
  private final RedissonCache redissonCache;

  @Inject
  public CustomerServiceClientCacheImpl(CustomerServiceClient customerServiceClient, RedissonClient redissonClient) {
    this.customerServiceClient = customerServiceClient;
    this.redissonCache = new RedissonCache(redissonClient, "dashboard", "customerServiceClient");
  }

  @Override
  public CustomerView getCustomer(String customerId) {
    return redissonCache.get(() -> customerServiceClient.getCustomer(customerId), "getCustomer", customerId);
  }

  @Override
  public SiteView getSite(String siteId) {
    return redissonCache.get(() -> customerServiceClient.getSite(siteId), "getSite", siteId);
  }
}
