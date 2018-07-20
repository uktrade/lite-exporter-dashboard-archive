package components.service.ogelonly;

import components.service.DestinationService;
import models.Application;

//Prevents binding of CountryProvider
public class OgelOnlyDestinationServiceImpl implements DestinationService {
  @Override
  public String getDestination(Application application) {
    return null;
  }
}
