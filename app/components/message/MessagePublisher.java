package components.message;

import uk.gov.bis.lite.exporterdashboard.api.RoutingKey;
import uk.gov.bis.lite.exporterdashboard.api.ExporterDashboardMessage;

public interface MessagePublisher {

  void sendMessage(RoutingKey routingKey, ExporterDashboardMessage exporterDashboardMessage);

}
