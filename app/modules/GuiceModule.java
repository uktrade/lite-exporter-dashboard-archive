package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import components.client.CustomerServiceClient;
import components.client.CustomerServiceClientImpl;
import components.client.OgelServiceClient;
import components.client.OgelServiceClientImpl;
import components.client.PermissionsServiceClient;
import components.client.PermissionsServiceClientImpl;
import components.common.journey.JourneyContextParamProvider;
import components.common.journey.JourneyDefinitionBuilder;
import components.common.journey.JourneySerialiser;
import components.common.state.ContextParamManager;
import components.common.transaction.TransactionContextParamProvider;
import components.dao.AmendmentDao;
import components.dao.AmendmentDaoImpl;
import components.dao.ApplicationDao;
import components.dao.ApplicationDaoImpl;
import components.dao.RfiDao;
import components.dao.RfiDaoImpl;
import components.dao.RfiResponseDao;
import components.dao.RfiResponseDaoImpl;
import components.dao.StatusUpdateDao;
import components.dao.StatusUpdateDaoImpl;
import components.dao.WithdrawalRequestDao;
import components.dao.WithdrawalRequestDaoImpl;
import components.mock.JourneyDefinitionBuilderMock;
import components.mock.JourneySerialiserMock;
import components.service.ApplicationItemViewService;
import components.service.ApplicationItemViewServiceImpl;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationSummaryViewServiceImpl;
import components.service.CacheService;
import components.service.CacheServiceImpl;
import components.service.OgelDetailsViewService;
import components.service.OgelDetailsViewServiceImpl;
import components.service.OgelRegistrationItemViewService;
import components.service.OgelRegistrationItemViewServiceImpl;
import components.service.OgelRegistrationService;
import components.service.OgelRegistrationServiceMockImpl;
import components.service.PageService;
import components.service.PageServiceImpl;
import components.service.PersonService;
import components.service.PersonServiceMock;
import components.service.ProcessingDescriptionService;
import components.service.ProcessingDescriptionServiceImpl;
import components.service.ProcessingLabelService;
import components.service.ProcessingLabelServiceImpl;
import components.service.RfiViewService;
import components.service.RfiViewServiceImpl;
import components.service.SortDirectionService;
import components.service.SortDirectionServiceImpl;
import components.service.StatusExplanationService;
import components.service.StatusExplanationServiceImpl;
import components.service.StatusItemViewService;
import components.service.StatusItemViewServiceImpl;
import components.service.StatusService;
import components.service.StatusServiceImpl;
import components.service.TestDataService;
import components.service.TestDataServiceImpl;
import components.service.TimeFormatService;
import components.service.TimeFormatServiceImpl;
import components.service.UserService;
import components.service.UserServiceMockImpl;
import components.service.WorkingDaysCalculatorService;
import components.service.WorkingDaysCalculatorServiceImpl;
import org.skife.jdbi.v2.DBI;
import play.Configuration;
import play.Environment;
import play.db.Database;

import java.util.Collection;
import java.util.Collections;

public class GuiceModule extends AbstractModule {

  private final Environment environment;
  private final Configuration configuration;

  public GuiceModule(Environment environment, Configuration configuration) {
    this.environment = environment;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    // CustomerServiceClient
    bindConstant().annotatedWith(Names.named("customerServiceAddress"))
        .to(configuration.getString("customerService.address"));
    bindConstant().annotatedWith(Names.named("customerServiceTimeout"))
        .to(configuration.getString("customerService.timeout"));
    bind(CustomerServiceClient.class).to(CustomerServiceClientImpl.class);
    // PermissionsServiceClient
    bindConstant().annotatedWith(Names.named("permissionsServiceAddress"))
        .to(configuration.getString("permissionsService.address"));
    bindConstant().annotatedWith(Names.named("permissionsServiceTimeout"))
        .to(configuration.getString("permissionsService.timeout"));
    bind(PermissionsServiceClient.class).to(PermissionsServiceClientImpl.class);
    // OgelServiceClient
    bindConstant().annotatedWith(Names.named("ogelServiceAddress"))
        .to(configuration.getString("ogelService.address"));
    bindConstant().annotatedWith(Names.named("ogelServiceTimeout"))
        .to(configuration.getString("ogelService.timeout"));
    bind(OgelServiceClient.class).to(OgelServiceClientImpl.class);
    // LicenseApplication
    bindConstant().annotatedWith(Names.named("licenceApplicationAddress"))
        .to(configuration.getString("licenceApplication.address"));
    // Service
    bind(JourneySerialiser.class).to(JourneySerialiserMock.class);
    bind(TimeFormatService.class).to(TimeFormatServiceImpl.class);
    bind(StatusService.class).to(StatusServiceImpl.class);
    bind(StatusItemViewService.class).to(StatusItemViewServiceImpl.class);
    bind(StatusExplanationService.class).to(StatusExplanationServiceImpl.class);
    bind(ProcessingLabelService.class).to(ProcessingLabelServiceImpl.class);
    bind(WorkingDaysCalculatorService.class).to(WorkingDaysCalculatorServiceImpl.class);
    bind(ProcessingDescriptionService.class).to(ProcessingDescriptionServiceImpl.class);
    bind(PersonService.class).to(PersonServiceMock.class);
    bind(RfiViewService.class).to(RfiViewServiceImpl.class);
    bind(UserService.class).to(UserServiceMockImpl.class);
    bind(OgelRegistrationService.class).to(OgelRegistrationServiceMockImpl.class).in(Singleton.class);
    bind(ApplicationItemViewService.class).to(ApplicationItemViewServiceImpl.class);
    bind(ApplicationSummaryViewService.class).to(ApplicationSummaryViewServiceImpl.class);
    bind(OgelRegistrationItemViewService.class).to(OgelRegistrationItemViewServiceImpl.class);
    bind(CacheService.class).to(CacheServiceImpl.class);
    bind(SortDirectionService.class).to(SortDirectionServiceImpl.class);
    bind(PageService.class).to(PageServiceImpl.class);
    bind(OgelDetailsViewService.class).to(OgelDetailsViewServiceImpl.class);
    // Database
    bind(RfiDao.class).to(RfiDaoImpl.class);
    bind(RfiResponseDao.class).to(RfiResponseDaoImpl.class);
    bind(StatusUpdateDao.class).to(StatusUpdateDaoImpl.class);
    bind(ApplicationDao.class).to(ApplicationDaoImpl.class);
    bind(WithdrawalRequestDao.class).to(WithdrawalRequestDaoImpl.class);
    bind(AmendmentDao.class).to(AmendmentDaoImpl.class);
    // Database test data
    bind(TestDataService.class).to(TestDataServiceImpl.class);
  }

  @Provides
  public Collection<JourneyDefinitionBuilder> provideJourneyDefinitionBuilders() {
    return Collections.singletonList(new JourneyDefinitionBuilderMock());
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