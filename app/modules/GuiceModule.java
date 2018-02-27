package modules;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import components.auth.SamlModule;
import components.client.CustomerServiceClient;
import components.client.LicenceClient;
import components.client.OgelServiceClient;
import components.client.OgelServiceClientImpl;
import components.client.UserServiceClient;
import components.client.UserServiceClientImpl;
import components.client.test.TestCustomerServiceClientImpl;
import components.client.test.TestLicenceClientImpl;
import components.common.journey.JourneyContextParamProvider;
import components.common.journey.JourneyDefinitionBuilder;
import components.common.journey.JourneySerialiser;
import components.common.state.ContextParamManager;
import components.common.transaction.TransactionContextParamProvider;
import components.common.upload.UploadGuiceModule;
import components.dao.AmendmentRequestDao;
import components.dao.ApplicationDao;
import components.dao.BacklogDao;
import components.dao.CaseDetailsDao;
import components.dao.DraftFileDao;
import components.dao.NotificationDao;
import components.dao.OutcomeDao;
import components.dao.ReadDao;
import components.dao.RfiDao;
import components.dao.RfiReplyDao;
import components.dao.RfiWithdrawalDao;
import components.dao.StatusUpdateDao;
import components.dao.WithdrawalApprovalDao;
import components.dao.WithdrawalRejectionDao;
import components.dao.WithdrawalRequestDao;
import components.dao.impl.AmendmentRequestDaoImpl;
import components.dao.impl.ApplicationDaoImpl;
import components.dao.impl.BacklogDaoImpl;
import components.dao.impl.CaseDetailsDaoImpl;
import components.dao.impl.DraftFileDaoImpl;
import components.dao.impl.NotificationDaoImpl;
import components.dao.impl.OutcomeDaoImpl;
import components.dao.impl.ReadDaoImpl;
import components.dao.impl.RfiDaoImpl;
import components.dao.impl.RfiReplyDaoImpl;
import components.dao.impl.RfiWithdrawalDaoImpl;
import components.dao.impl.StatusUpdateDaoImpl;
import components.dao.impl.WithdrawalApprovalDaoImpl;
import components.dao.impl.WithdrawalRejectionDaoImpl;
import components.dao.impl.WithdrawalRequestDaoImpl;
import components.message.MessageHandler;
import components.message.MessageHandlerImpl;
import components.message.MessagePublisher;
import components.message.MessagePublisherImpl;
import components.message.SqsPoller;
import components.message.SqsPollerImpl;
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
import components.service.DraftFileService;
import components.service.DraftFileServiceImpl;
import components.service.MessageViewService;
import components.service.MessageViewServiceImpl;
import components.service.OfficerViewService;
import components.service.OfficerViewServiceImpl;
import components.service.OgelDetailsViewService;
import components.service.OgelItemViewService;
import components.service.PreviousRequestItemViewService;
import components.service.PreviousRequestItemViewServiceImpl;
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
import components.service.StatusTrackerViewService;
import components.service.StatusTrackerViewServiceImpl;
import components.service.UserPermissionService;
import components.service.UserService;
import components.service.WithdrawalRequestService;
import components.service.WithdrawalRequestServiceImpl;
import components.service.test.TestDataService;
import components.service.test.TestDataServiceImpl;
import components.service.test.TestOgelDetailsViewServiceImpl;
import components.service.test.TestOgelItemViewServiceImpl;
import components.service.test.TestUserPermissionServiceImpl;
import components.service.test.TestUserServiceImpl;
import filters.common.JwtRequestFilterConfig;
import org.apache.commons.lang3.StringUtils;
import org.skife.jdbi.v2.DBI;
import play.Configuration;
import play.Environment;
import play.db.Database;

import java.util.Collection;
import java.util.Collections;

public class GuiceModule extends AbstractModule {

  private static final String ISSUER = "lite-exporter-dashboard";

  private final Environment environment;
  private final Configuration configuration;

  public GuiceModule(Environment environment, Configuration configuration) {
    this.environment = environment;
    this.configuration = configuration;
  }

  @Override
  protected void configure() {
    install(new SamlModule(configuration));
    bindClients();
    // Jwt
    bindConstant().annotatedWith(Names.named("jwtSharedSecret")).to(configuration.getString("jwtSharedSecret"));
    // LicenceApplication
    bindConstant().annotatedWith(Names.named("licenceApplicationAddress")).to(configuration.getString("licenceApplication.address"));
    // Service
    bind(JourneySerialiser.class).to(JourneySerialiserMock.class);
    bind(StatusTrackerViewService.class).to(StatusTrackerViewServiceImpl.class);
    bind(RfiViewService.class).to(RfiViewServiceImpl.class);
    // TODO Test
    bind(UserService.class).to(TestUserServiceImpl.class);
    bind(ApplicationItemViewService.class).to(ApplicationItemViewServiceImpl.class);
    bind(ApplicationSummaryViewService.class).to(ApplicationSummaryViewServiceImpl.class);
    // TODO Test
    bind(OgelItemViewService.class).to(TestOgelItemViewServiceImpl.class);
    // TODO Test
    bind(OgelDetailsViewService.class).to(TestOgelDetailsViewServiceImpl.class);
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
    bind(PreviousRequestItemViewService.class).to(PreviousRequestItemViewServiceImpl.class);
    bind(UserPermissionService.class).to(TestUserPermissionServiceImpl.class).asEagerSingleton();
    bind(DraftFileService.class).to(DraftFileServiceImpl.class);
    // Database
    bind(RfiDao.class).to(RfiDaoImpl.class);
    bind(RfiReplyDao.class).to(RfiReplyDaoImpl.class);
    bind(StatusUpdateDao.class).to(StatusUpdateDaoImpl.class);
    bind(ApplicationDao.class).to(ApplicationDaoImpl.class);
    bind(WithdrawalRequestDao.class).to(WithdrawalRequestDaoImpl.class);
    bind(AmendmentRequestDao.class).to(AmendmentRequestDaoImpl.class);
    bind(DraftFileDao.class).to(DraftFileDaoImpl.class).asEagerSingleton();
    bind(OutcomeDao.class).to(OutcomeDaoImpl.class);
    bind(NotificationDao.class).to(NotificationDaoImpl.class);
    bind(WithdrawalRejectionDao.class).to(WithdrawalRejectionDaoImpl.class);
    bind(WithdrawalApprovalDao.class).to(WithdrawalApprovalDaoImpl.class);
    bind(RfiWithdrawalDao.class).to(RfiWithdrawalDaoImpl.class);
    bind(ReadDao.class).to(ReadDaoImpl.class);
    bind(CaseDetailsDao.class).to(CaseDetailsDaoImpl.class);
    bind(BacklogDao.class).to(BacklogDaoImpl.class);
    // Database test data
    // TODO Test
    bind(TestDataService.class).to(TestDataServiceImpl.class);
    // Start up
    bind(StartUpService.class).to(StartUpServiceImpl.class).asEagerSingleton();
    // Amazon
    bindSnsAndSqsServices();
    // Upload
    install(new UploadGuiceModule(configuration));
    // Basic auth
    bindConstant().annotatedWith(Names.named("basicAuthUser")).to(configuration.getString("basicAuth.user"));
    bindConstant().annotatedWith(Names.named("basicAuthPassword")).to(configuration.getString("basicAuth.password"));
    bindConstant().annotatedWith(Names.named("basicAuthRealm")).to(configuration.getString("basicAuth.realm"));
  }

  private void bindSnsAndSqsServices() {
    String region = configuration.getString("aws.region");
    AWSCredentialsProvider awsCredentialsProvider = getAwsCredentials();
    // Sqs and Sns
    bindConstant().annotatedWith(Names.named("awsSnsTopicArn")).to(configuration.getString("aws.snsTopicArn"));
    bindConstant().annotatedWith(Names.named("awsSqsWaitTimeSeconds")).to(configuration.getString("aws.sqsWaitTimeSeconds"));
    bindConstant().annotatedWith(Names.named("awsSqsQueueUrl")).to(configuration.getString("aws.sqsQueueUrl"));
    AmazonSQS amazonSQS = AmazonSQSClientBuilder.standard()
        .withRegion(region)
        .withCredentials(awsCredentialsProvider)
        .build();
    bind(AmazonSQS.class).toInstance(amazonSQS);
    AmazonSNS amazonSNS = AmazonSNSClientBuilder.standard()
        .withRegion(region)
        .withCredentials(awsCredentialsProvider)
        .build();
    bind(AmazonSNS.class).toInstance(amazonSNS);
    bind(SqsPoller.class).to(SqsPollerImpl.class).asEagerSingleton();
    bind(MessagePublisher.class).to(MessagePublisherImpl.class);
    bind(MessageHandler.class).to(MessageHandlerImpl.class);
  }

  private AWSCredentialsProvider getAwsCredentials() {
    String profileName = configuration.getString("aws.credentials.profileName");
    String accessKey = configuration.getString("aws.credentials.accessKey");
    String secretKey = configuration.getString("aws.credentials.secretKey");
    if (StringUtils.isNoneBlank(profileName)) {
      return new ProfileCredentialsProvider(profileName);
    } else if (StringUtils.isBlank(accessKey) || StringUtils.isBlank(secretKey)) {
      throw new RuntimeException("accessKey and secretKey must both be specified if no profile name is specified");
    } else {
      return new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey));
    }
  }

  private void bindClients() {
    // CustomerServiceClient
    bindConstant().annotatedWith(Names.named("customerServiceAddress")).to(configuration.getString("customerService.address"));
    bindConstant().annotatedWith(Names.named("customerServiceTimeout")).to(configuration.getString("customerService.timeout"));
    // TODO Test
    bind(CustomerServiceClient.class).to(TestCustomerServiceClientImpl.class);
    // LicenceClient
    bindConstant().annotatedWith(Names.named("permissionsServiceAddress")).to(configuration.getString("permissionsService.address"));
    bindConstant().annotatedWith(Names.named("permissionsServiceTimeout")).to(configuration.getString("permissionsService.timeout"));
    // TODO Test
    bind(LicenceClient.class).to(TestLicenceClientImpl.class);
    // OgelServiceClient
    bindConstant().annotatedWith(Names.named("ogelServiceAddress")).to(configuration.getString("ogelService.address"));
    bindConstant().annotatedWith(Names.named("ogelServiceTimeout")).to(configuration.getString("ogelService.timeout"));
    bindConstant().annotatedWith(Names.named("ogelServiceCredentials")).to(configuration.getString("ogelService.credentials"));
    bind(OgelServiceClient.class).to(OgelServiceClientImpl.class);
    // UserServiceClient
    bindConstant().annotatedWith(Names.named("userServiceAddress")).to(configuration.getString("userService.address"));
    bindConstant().annotatedWith(Names.named("userServiceTimeout")).to(configuration.getString("userService.timeout"));
    bindConstant().annotatedWith(Names.named("userServiceCacheExpiryMinutes")).to(configuration.getString("userService.cacheExpiryMinutes"));

    bind(UserServiceClient.class).to(UserServiceClientImpl.class);
  }

  @Provides
  public JwtRequestFilterConfig provideJwtRequestFilterConfig(@Named("jwtSharedSecret") String key) {
    return new JwtRequestFilterConfig(key, ISSUER);
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
  public DBI provideDataSourceDbi(Configuration configuration, Database database) {
    return new DBI(database.getUrl(),
        configuration.getString("db.default.username"),
        configuration.getString("db.default.password"));
  }

}
