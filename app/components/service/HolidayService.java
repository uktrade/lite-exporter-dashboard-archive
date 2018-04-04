package components.service;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {

  List<LocalDate> loadHolidaysFromFile(String path);

}
