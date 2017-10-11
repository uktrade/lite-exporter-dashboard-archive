package modules;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import com.rabbitmq.client.Channel;
import components.auth.SamlModule;
import components.client.CustomerServiceClient;
import components.client.OgelServiceClient;
import components.client.OgelServiceClientImpl;
import components.client.PermissionsServiceClient;
import components.client.test.TestCustomerServiceClientImpl;
import components.client.test.TestPermissionsServiceClientImpl;
import components.common.journey.JourneyContextParamProvider;
import components.common.journey.JourneyDefinitionBuilder;
import components.common.journey.JourneySerialiser;
import components.common.state.ContextParamManager;
import components.common.transaction.TransactionContextParamProvider;
import components.dao.AmendmentDao;
import components.dao.AmendmentDaoImpl;
import components.dao.ApplicationDao;
import components.dao.ApplicationDaoImpl;
import components.dao.DraftDao;
import components.dao.DraftDaoImpl;
import components.dao.NotificationDao;
import components.dao.NotificationDaoImpl;
import components.dao.OutcomeDao;
import components.dao.OutcomeDaoImpl;
import components.dao.ReadDao;
import components.dao.ReadDaoImpl;
import components.dao.RfiDao;
import components.dao.RfiDaoImpl;
import components.dao.RfiReplyDao;
import components.dao.RfiReplyDaoImpl;
import components.dao.RfiWithdrawalDao;
import components.dao.RfiWithdrawalDaoImpl;
import components.dao.SielDao;
import components.dao.SielDaoImpl;
import components.dao.StatusUpdateDao;
import components.dao.StatusUpdateDaoImpl;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalApprovalDaoImpl;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRejectionDaoImpl;
import components.dao.WithdrawalRequestDao;
import components.dao.WithdrawalRequestDaoImpl;
import components.message.ConnectionManager;
import components.message.ConnectionManagerImpl;
import components.message.MessageConsumer;
import components.message.MessageConsumerImpl;
import components.message.MessagePublisher;
import components.message.MessagePublisherImpl;
import components.message.QueueManager;
import components.message.QueueManagerImpl;
import components.mock.JourneyDefinitionBuilderMock;
import components.mock.JourneySerialiserMock;
import components.service.AmendmentService;
import components.service.AmendmentServiceImpl;
import components.service.AppDataService;
import components.service.AppDataServiceImpl;
import components.service.ApplicationItemViewService;
import components.service.ApplicationItemViewServiceImpl;
import components.service.ApplicationSummaryViewService;
import components.service.ApplicationSummaryViewServiceImpl;
import components.service.ApplicationTabsViewService;
import components.service.ApplicationTabsViewServiceImpl;
import components.service.MessageViewService;
import components.service.MessageViewServiceImpl;
import components.service.OfficerViewService;
import components.service.OfficerViewServiceImpl;
import components.service.OgelDetailsViewService;
import components.service.OgelDetailsViewServiceImpl;
import components.service.OgelItemViewService;
import components.service.ReadDataService;
import components.service.ReadDataServiceImpl;
import components.service.RfiReplyService;
import components.service.RfiReplyServiceImpl;
import components.service.RfiViewService;
import components.service.RfiViewServiceImpl;
import components.service.SielDetailsViewService;
import components.service.SielDetailsViewServiceImpl;
import components.service.SielItemViewService;
import components.service.SielItemViewServiceImpl;
import components.service.StartUpService;
import components.service.StartUpServiceImpl;
import components.service.StatusItemViewService;
import components.service.StatusItemViewServiceImpl;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.service.WithdrawalRequestServiceImpl;
import components.service.test.TestDataService;
import components.service.test.TestDataServiceImpl;
import components.service.test.TestOgelItemViewServiceImpl;
import components.service.test.TestUserServiceImpl;
import java.util.Collection;
import java.util.Collections;
import org.skife.jdbi.v2.DBI;
import play.Configuration;
import play.Environment;
import play.db.Database;

public class GuiceModule extends AbstractModule {

  private final Environment environment;
  private final Configuration configuration;

  public GuiceModule(Environment environment, Configuration configuration) {
    this.environment = environment;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {

    install(new SamlModule(configuration));

    // Upload
    bindConstant("uploadFolder", "upload.folder");
    // CustomerServiceClient
    bindConstant("customerServiceAddress", "customerService.address");
    bindConstant("customerServiceTimeout", "customerService.timeout");
    // TODO Test
    bind(CustomerServiceClient.class).to(TestCustomerServiceClientImpl.class);
    // PermissionsServiceClient
    bindConstant("permissionsServiceAddress", "permissionsService.address");
    bindConstant("permissionsServiceTimeout", "permissionsService.timeout");
    // TODO Test
    bind(PermissionsServiceClient.class).to(TestPermissionsServiceClientImpl.class);
    // OgelServiceClient
    bindConstant("ogelServiceAddress", "ogelService.address");
    bindConstant("ogelServiceTimeout", "ogelService.timeout");
    bind(OgelServiceClient.class).to(OgelServiceClientImpl.class);
    // LicenceApplication
    bindConstant("licenceApplicationAddress", "licenceApplication.address");
    // Service
    bind(JourneySerialiser.class).to(JourneySerialiserMock.class);
    bind(StatusItemViewService.class).to(StatusItemViewServiceImpl.class);
    bind(RfiViewService.class).to(RfiViewServiceImpl.class);
    // TODO Test
    bind(UserService.class).to(TestUserServiceImpl.class);
    bind(ApplicationItemViewService.class).to(ApplicationItemViewServiceImpl.class);
    bind(ApplicationSummaryViewService.class).to(ApplicationSummaryViewServiceImpl.class);
    // TODO Test
    bind(OgelItemViewService.class).to(TestOgelItemViewServiceImpl.class);
    bind(OgelDetailsViewService.class).to(OgelDetailsViewServiceImpl.class);
    bind(SielDetailsViewService.class).to(SielDetailsViewServiceImpl.class);
    bind(OfficerViewService.class).to(OfficerViewServiceImpl.class);
    bind(AmendmentService.class).to(AmendmentServiceImpl.class);
    bind(WithdrawalRequestService.class).to(WithdrawalRequestServiceImpl.class);
    bind(RfiReplyService.class).to(RfiReplyServiceImpl.class);
    bind(SielItemViewService.class).to(SielItemViewServiceImpl.class);
    bind(MessageViewService.class).to(MessageViewServiceImpl.class);
    bind(AppDataService.class).to(AppDataServiceImpl.class);
    bind(ReadDataService.class).to(ReadDataServiceImpl.class);
    bind(ApplicationTabsViewService.class).to(ApplicationTabsViewServiceImpl.class);
    // Database
    bind(RfiDao.class).to(RfiDaoImpl.class);
    bind(RfiReplyDao.class).to(RfiReplyDaoImpl.class);
    bind(StatusUpdateDao.class).to(StatusUpdateDaoImpl.class);
    bind(ApplicationDao.class).to(ApplicationDaoImpl.class);
    bind(WithdrawalRequestDao.class).to(WithdrawalRequestDaoImpl.class);
    bind(AmendmentDao.class).to(AmendmentDaoImpl.class);
    bind(DraftDao.class).to(DraftDaoImpl.class).asEagerSingleton();
    bind(SielDao.class).to(SielDaoImpl.class);
    bind(OutcomeDao.class).to(OutcomeDaoImpl.class);
    bind(NotificationDao.class).to(NotificationDaoImpl.class);
    bind(WithdrawalRejectionDao.class).to(WithdrawalRejectionDaoImpl.class);
    bind(WithdrawalApprovalDao.class).to(WithdrawalApprovalDaoImpl.class);
    bind(RfiWithdrawalDao.class).to(RfiWithdrawalDaoImpl.class);
    bind(ReadDao.class).to(ReadDaoImpl.class);
    // Database test data
    // TODO Test
    bind(TestDataService.class).to(TestDataServiceImpl.class);
    // Start up
    bind(StartUpService.class).to(StartUpServiceImpl.class).asEagerSingleton();
    // Queue
    boolean enabled = configuration.getBoolean("spireRelayService.enabled", false);
    if (enabled) {
      bindConstant("consumerExchangeName", "spireRelayService.consumerExchangeName");
      bindConstant("publisherExchangeName", "spireRelayService.publisherExchangeName");
      bindConstant("rabbitMqUrl", "spireRelayService.rabbitMqUrl");
      bindConstant("consumerQueueName", "spireRelayService.consumerQueueName");
      bindConstant("publisherQueueName", "spireRelayService.publisherQueueName");
      bind(ConnectionManager.class).to(ConnectionManagerImpl.class).asEagerSingleton();
      bind(QueueManager.class).to(QueueManagerImpl.class).asEagerSingleton();
      bind(MessageConsumer.class).to(MessageConsumerImpl.class).asEagerSingleton();
      bind(MessagePublisher.class).to(MessagePublisherImpl.class);
    } else {
      bind(ConnectionManager.class).toInstance(() -> null);
      bind(MessagePublisher.class).toInstance((routingKey, object) -> {
      });
    }
  }

  private void bindConstant(String name, String configKey) {
    bindConstant().annotatedWith(Names.named(name)).to(configuration.getString(configKey));
  }

  @Provides
  @Singleton
  public Channel channel(ConnectionManager connectionManager) {
    return connectionManager.createChannel();
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
