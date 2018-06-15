package components.service.ogelonly;

import components.message.MessagePublisher;
import uk.gov.bis.lite.exporterdashboard.api.ExporterDashboardMessage;
import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;

public class OgelOnlyMessagePublisher implements MessagePublisher {

  @Override
  public void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage) {
    // do nothing
  }

}
