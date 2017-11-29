package components.util;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;

public class TimeUtil {

  private static final DateTimeFormatter ogelDateFormatter = new DateTimeFormatterBuilder()
      .appendPattern("yyyy-MM-dd")
      .parseDefaulting(ChronoField.NANO_OF_DAY, 0).toFormatter()
      .withZone(ZoneId.systemDefault());
  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy").withZone(ZoneId.systemDefault());
  private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("h:mm")
      .appendText(ChronoField.AMPM_OF_DAY, ImmutableMap.of(0L, "am", 1L, "pm"))
      .toFormatter()
      .withZone(ZoneId.systemDefault());

  public static String formatDateAndTime(long millis) {
    String date = dateFormatter.format(Instant.ofEpochMilli(millis));
    String time = timeFormatter.format(Instant.ofEpochMilli(millis));
    return date + " at " + time;
  }

  public static long parseOgelRegistrationDate(String ogelRegistrationDate) {
    TemporalAccessor temporalAccessor = ogelDateFormatter.parse(ogelRegistrationDate);
    return Instant.from(temporalAccessor).toEpochMilli();
  }

  public static String formatDate(long millis) {
    return dateFormatter.format(Instant.ofEpochMilli(millis));
  }

  public static long time(int year, int month, int dayOfMonth, int hour, int minute) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static long daysBetweenWithStartBeforeEnd(long start, long end) {
    return ChronoUnit.DAYS.between(Instant.ofEpochMilli(start), Instant.ofEpochMilli(end));
  }

  public static long toMillis(LocalDate localDate) {
    return localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
  }

  public static LocalDate toLocalDate(long millis) {
    return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate();
  }

}
