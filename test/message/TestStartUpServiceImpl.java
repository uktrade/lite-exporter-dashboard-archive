package message;

import com.google.inject.Inject;
import components.service.StartUpService;
import net.sf.ehcache.CacheManager;
import play.inject.ApplicationLifecycle;

import java.util.concurrent.CompletableFuture;

// https://stackoverflow.com/a/43243294
public class TestStartUpServiceImpl implements StartUpService {

  @Inject
  public TestStartUpServiceImpl(ApplicationLifecycle lifecycle) {
    lifecycle.addStopHook(() -> {
      CacheManager.getInstance().shutdown();
      return CompletableFuture.completedFuture(null);
    });
  }

}
