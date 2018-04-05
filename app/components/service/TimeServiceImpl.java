package components.service;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

public class TimeServiceImpl implements TimeService {

  private final ZoneId zoneId;
  private final DateTimeFormatter yearMonthDayFormatter;
  private final DateTimeFormatter dateFormatter;
  private final DateTimeFormatter timeFormatter;

  @Inject
  public TimeServiceImpl(ZoneId zoneId) {
    this.zoneId = zoneId;
    this.yearMonthDayFormatter = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd")
        .toFormatter()
        .withZone(zoneId);
    this.dateFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
        .withZone(zoneId);
    this.timeFormatter = new DateTimeFormatterBuilder()
        .appendPattern("h:mm")
        .appendText(ChronoField.AMPM_OF_DAY, ImmutableMap.of(0L, "am", 1L, "pm"))
        .toFormatter()
        .withZone(zoneId);
  }

  @Override
  public String formatDateAndTime(long millis) {
    String date = dateFormatter.format(Instant.ofEpochMilli(millis));
    String time = timeFormatter.format(Instant.ofEpochMilli(millis));
    return date + " at " + time;
  }

  @Override
  public LocalDate parseYearMonthDate(String date) {
    return LocalDate.parse(date, yearMonthDayFormatter);
  }

  @Override
  public String formatDate(long millis) {
    return dateFormatter.format(Instant.ofEpochMilli(millis));
  }

  @Override
  public long time(int year, int month, int dayOfMonth, int hour, int minute) {
    return LocalDateTime.of(year, month, dayOfMonth, hour, minute).atZone(zoneId).toInstant().toEpochMilli();
  }

  @Override
  public long daysBetweenWithStartBeforeEnd(long start, long end) {
    return toLocalDate(start).until(toLocalDate(end), ChronoUnit.DAYS);
  }

  @Override
  public long toMillis(LocalDate localDate) {
    return localDate.atStartOfDay().atZone(zoneId).toInstant().toEpochMilli();
  }

  @Override
  public LocalDate toLocalDate(long millis) {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDate();
  }

  @Override
  public LocalDateTime toLocalDateTime(long millis) {
    return Instant.ofEpochMilli(millis).atZone(zoneId).toLocalDateTime();
  }

}
