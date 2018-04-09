package components.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import components.exceptions.ObjectMapperException;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayServiceImpl implements HolidayService {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final TimeService timeService;

  @Inject
  public HolidayServiceImpl(TimeService timeService) {
    this.timeService = timeService;
  }

  @Override
  public List<LocalDate> loadHolidaysFromFile(String path) {
    List<LocalDate> localDates = new ArrayList<>();
    JsonNode jsonNode;
    try {
      String json = IOUtils.toString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
      jsonNode = MAPPER.readTree(json);
    } catch (IOException ioe) {
      throw new ObjectMapperException("Unable to read holiday json from path " + path, ioe);
    }
    JsonNode events = jsonNode.get("england-and-wales").get("events");
    for (int i = 0; i < events.size(); i++) {
      String date = events.get(i).get("date").asText();
      localDates.add(timeService.parseYearMonthDate(date));
    }
    return localDates;
  }

}
