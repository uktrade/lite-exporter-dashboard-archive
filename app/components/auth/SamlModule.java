package components.auth;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import components.common.auth.SamlUtil;
import components.common.auth.SpireAuthManager;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlayCacheStore;
import play.Application;
import play.Configuration;
import play.api.cache.CacheApi;

import java.util.concurrent.TimeUnit;

public class SamlModule extends AbstractModule {

  private final Configuration configuration;

  public SamlModule(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    ApplicationLogoutController logoutController = new ApplicationLogoutController();
    logoutController.setDefaultUrl(controllers.routes.AuthorisationController.loggedOut().url());
    bind(ApplicationLogoutController.class).toInstance(logoutController);
  }

  @Singleton
  @Provides
  public Config provideConfig(SessionStore<PlayWebContext> playCacheStore,
                              SamlAuthorizer samlAuthorizer,
                              SpireAuthManager authManager,
                              Provider<Application> application) {
    return SamlUtil.buildConfig(application,
        configuration,
        authManager,
        playCacheStore,
        new SamlHttpActionAdaptor(),
        ImmutableMap.of(SamlAuthorizer.AUTHORIZER_NAME, samlAuthorizer));
  }

  @Provides
  public SessionStore<PlayWebContext> providePlayCacheStore(CacheApi cacheApi) {

    PlayCacheStore playCacheStore = new PlayCacheStore();
    //How long the Saml profile is stored in the cache
    playCacheStore.setProfileTimeout((int) TimeUnit.HOURS.toSeconds(6));

    //How long session attributes such as return URL are stored in the cache
    playCacheStore.setSessionTimeout((int) TimeUnit.MINUTES.toSeconds(15));

    return playCacheStore;
  }

}
