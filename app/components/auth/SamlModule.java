package components.auth;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import components.common.auth.SamlUtil;
import org.pac4j.play.LogoutController;
import org.pac4j.play.store.PlayCacheSessionStore;
import org.pac4j.play.store.PlaySessionStore;
import play.cache.SyncCacheApi;

import java.util.concurrent.TimeUnit;

public class SamlModule extends AbstractModule {

  private final Config config;

  public SamlModule(Config config) {
    this.config = config;
  }

  @Override
  protected void configure() {
    LogoutController logoutController = new LogoutController();
    logoutController.setDefaultUrl(controllers.routes.AuthorisationController.loggedOut().url());
    bind(LogoutController.class).toInstance(logoutController);
  }

  @Singleton
  @Provides
  public PlaySessionStore providePlaySessionStore(SyncCacheApi syncCacheApi) {
    PlayCacheSessionStore playCacheSessionStore = new PlayCacheSessionStore(syncCacheApi);
    playCacheSessionStore.setTimeout((int) TimeUnit.MINUTES.toSeconds(15));
    return playCacheSessionStore;
  }

  @Singleton
  @Provides
  public org.pac4j.core.config.Config provideConfig(PlaySessionStore playSessionStore,
                                                    SamlAuthorizer samlAuthorizer) {
    return SamlUtil.buildConfig(config,
        playSessionStore,
        new SamlHttpActionAdaptor(),
        ImmutableMap.of(SamlAuthorizer.AUTHORIZER_NAME, samlAuthorizer));
  }

}
