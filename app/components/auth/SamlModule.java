package components.auth;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import components.common.auth.SpireAuthManager;
import components.common.auth.SpireSAML2Client;
import org.pac4j.core.client.Clients;
import org.pac4j.core.config.Config;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.play.ApplicationLogoutController;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.store.PlayCacheStore;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.client.SAML2ClientConfiguration;
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
                              SpireAuthManager authManager) {

    //Keystore with a dummy certificate: the outgoing request is not signature checked
    SAML2ClientConfiguration samlConfig = new SAML2ClientConfiguration("resource:saml/keystore.jks",
        "keypass",
        "keypass",
        configuration.getString("saml.metadataFile"));

    //Maximum permitted age of IdP response in seconds
    samlConfig.setMaximumAuthenticationLifetime(3600);

    //AKA Saml2:Issuer
    samlConfig.setServiceProviderEntityId(configuration.getString("saml.issuer"));
    samlConfig.setServiceProviderMetadataPath("");

    //Custom SAML client which will store response attributes on the Saml Profile
    SAML2Client saml2Client = new SpireSAML2Client(samlConfig, authManager);

    Clients clients = new Clients(configuration.getString("saml.callbackUrl"), saml2Client);

    clients.setDefaultClient(saml2Client);
    saml2Client.setCallbackUrl(configuration.getString("saml.callbackUrl"));
    saml2Client.setIncludeClientNameInCallbackUrl(false);

    Config config = new Config(clients);

    config.setHttpActionAdapter(new SamlHttpActionAdaptor());

    config.setSessionStore(playCacheStore);

    config.addAuthorizer(SamlAuthorizer.AUTHORIZER_NAME, samlAuthorizer);

    return config;
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
