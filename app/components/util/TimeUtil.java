package components.util;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class TimeUtil {

  public static long time(int year, int month, int dayOfMonth, int hour, int minute) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

}
