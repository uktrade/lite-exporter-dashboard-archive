package components.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TimeService {
  String formatDateAndTime(long millis);

  LocalDate parseYearMonthDate(String date);

  String formatDate(long millis);

  long time(int year, int month, int dayOfMonth, int hour, int minute);

  long daysBetweenWithStartBeforeEnd(long start, long end);

  long toMillis(LocalDate localDate);

  LocalDate toLocalDate(long millis);

  LocalDateTime toLocalDateTime(long millis);
}
