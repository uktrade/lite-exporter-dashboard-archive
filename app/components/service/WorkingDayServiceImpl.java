package components.service;

import components.util.TimeUtil;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WorkingDayServiceImpl implements WorkingDayService {

  private final Set<LocalDate> holidays;

  public WorkingDayServiceImpl(List<LocalDate> holidays) {
    this.holidays = new HashSet<>(holidays);
  }

  @Override
  public int calculateWorkingDays(long start, long end) {
    if (start >= end) {
      return 0;
    } else {
      LocalDateTime startLocalDate = TimeUtil.toLocalDateTime(start);
      LocalDateTime endLocalDate = TimeUtil.toLocalDateTime(end);

      int days = 0;
      LocalDateTime now = startLocalDate;

      while (endLocalDate.isAfter(now)) {
        now = now.plusDays(1);
        if (!isWeekend(now) && !isBankHoliday(now)) {
          days++;
        }
      }
      if (days == 0) {
        days = 1;
      }
      return days;
    }
  }

  private boolean isWeekend(LocalDateTime localDateTime) {
    return localDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || localDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
  }

  private boolean isBankHoliday(LocalDateTime localDateTime) {
    return holidays.contains(localDateTime.toLocalDate());
  }

}
