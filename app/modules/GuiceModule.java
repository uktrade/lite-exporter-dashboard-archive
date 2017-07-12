package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import components.common.journey.JourneyContextParamProvider;
import components.common.journey.JourneySerialiser;
import components.common.journey.JourneyDefinitionBuilder;
import components.common.state.ContextParamManager;
import components.common.transaction.TransactionContextParamProvider;
import components.mock.JourneyDefinitionBuilderMock;
import components.mock.JourneySerialiserMock;
import play.Configuration;
import play.Environment;

import java.util.Arrays;
import java.util.Collection;

public class GuiceModule extends AbstractModule{

  private Environment environment;

  private Configuration configuration;

  public GuiceModule(Environment environment, Configuration configuration) {
    this.environment = environment;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    bind(JourneySerialiser.class).to(JourneySerialiserMock.class);
  }

  @Provides
  public Collection<JourneyDefinitionBuilder> provideJourneyDefinitionBuilders() {
    return Arrays.asList(new JourneyDefinitionBuilderMock());
  }

  @Provides
  public ContextParamManager provideContextParamManager() {
    return new ContextParamManager(new JourneyContextParamProvider(), new TransactionContextParamProvider());
  }
}