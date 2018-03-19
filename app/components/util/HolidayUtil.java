package components.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HolidayUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  public static List<LocalDate> loadHolidaysFromFile(String path) {
    List<LocalDate> localDates = new ArrayList<>();
    JsonNode jsonNode;
    try {
      String json = IOUtils.toString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
      jsonNode = MAPPER.readTree(json);
    } catch (IOException ioe) {
      throw new RuntimeException("Unable to read holiday json from path " + path, ioe);
    }
    JsonNode events = jsonNode.get("england-and-wales").get("events");
    for (int i = 0; i < events.size(); i++) {
      String date = events.get(i).get("date").asText();
      localDates.add(TimeUtil.parseYearMonthDate(date));
    }
    return localDates;
  }

}
