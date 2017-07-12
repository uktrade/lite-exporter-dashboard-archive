package components.mock;

import components.common.journey.*;
import controllers.routes;
import play.api.mvc.Call;

import java.util.Map;


public class JourneyDefinitionBuilderMock extends JourneyDefinitionBuilder {

    private final JourneyStage mockStage = defineStage("mockStage", "Mock stage", new Call("mock", "mock", "mock"));

    @Override
    protected void journeys() {

        atStage(mockStage);

        defineJourney("Mock stage", mockStage);
    }

}
