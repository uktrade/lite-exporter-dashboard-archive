package components.mock;

import com.google.inject.Inject;
import components.common.journey.JourneySerialiser;

public class JourneySerialiserMock implements JourneySerialiser {

  @Inject
  public JourneySerialiserMock() {

  }

  @Override
  public String readJourneyString(String journeyName) {
    return "Dummy string";
  }

  @Override
  public void writeJourneyString(String journeyName, String journeyString) {
  }

}
