package components.mock;

import components.common.journey.JourneyDefinitionBuilder;
import components.common.journey.JourneyStage;
import play.api.mvc.Call;


public class JourneyDefinitionBuilderMock extends JourneyDefinitionBuilder {

  private final JourneyStage mockStage = defineStage("mockStage", "Mock stage", new Call("mock", "mock", "mock"));

  @Override
  protected void journeys() {

    atStage(mockStage);

    defineJourney("Mock stage", mockStage);
  }

}
