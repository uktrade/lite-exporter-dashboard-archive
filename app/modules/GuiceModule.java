package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import components.common.journey.JourneyContextParamProvider;
import components.common.journey.JourneyDefinitionBuilder;
import components.common.journey.JourneySerialiser;
import components.common.state.ContextParamManager;
import components.common.transaction.TransactionContextParamProvider;
import components.dao.StatusUpdateDao;
import components.dao.StatusUpdateDaoImpl;
import components.mock.JourneyDefinitionBuilderMock;
import components.mock.JourneySerialiserMock;
import components.service.InsertTestDataService;
import components.service.InsertTestDataServiceImpl;
import components.service.ProcessingDescriptionService;
import components.service.ProcessingDescriptionServiceImpl;
import components.service.ProcessingLabelService;
import components.service.ProcessingLabelServiceImpl;
import components.service.StatusExplanationService;
import components.service.StatusExplanationServiceImpl;
import components.service.StatusItemViewService;
import components.service.StatusItemViewServiceImpl;
import components.service.StatusService;
import components.service.StatusServiceImpl;
import components.service.TimeFormatService;
import components.service.TimeFormatServiceImpl;
import components.service.WorkingDaysCalculatorService;
import components.service.WorkingDaysCalculatorServiceImpl;
import org.skife.jdbi.v2.DBI;
import play.Configuration;
import play.Environment;
import play.db.Database;

import java.util.Arrays;
import java.util.Collection;

public class GuiceModule extends AbstractModule {

  private final Environment environment;
  private final Configuration configuration;

  public GuiceModule(Environment environment, Configuration configuration) {
    this.environment = environment;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    bind(JourneySerialiser.class).to(JourneySerialiserMock.class);
    bind(TimeFormatService.class).to(TimeFormatServiceImpl.class);
    bind(StatusService.class).to(StatusServiceImpl.class);
    bind(StatusItemViewService.class).to(StatusItemViewServiceImpl.class);
    bind(StatusExplanationService.class).to(StatusExplanationServiceImpl.class);
    bind(ProcessingLabelService.class).to(ProcessingLabelServiceImpl.class);
    bind(WorkingDaysCalculatorService.class).to(WorkingDaysCalculatorServiceImpl.class);
    bind(ProcessingDescriptionService.class).to(ProcessingDescriptionServiceImpl.class);
    // Database
    bind(StatusUpdateDao.class).to(StatusUpdateDaoImpl.class);
    // Test data
    bind(InsertTestDataService.class).to(InsertTestDataServiceImpl.class);
  }

  @Provides
  public Collection<JourneyDefinitionBuilder> provideJourneyDefinitionBuilders() {
    return Arrays.asList(new JourneyDefinitionBuilderMock());
  }

  @Provides
  public ContextParamManager provideContextParamManager() {
    return new ContextParamManager(new JourneyContextParamProvider(), new TransactionContextParamProvider());
  }

  @Provides
  @Singleton
  public DBI provideDataSourceDbi(Database database) {
    return new DBI(database.getUrl());
  }
}