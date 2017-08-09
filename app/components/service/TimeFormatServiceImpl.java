package components.service;

import com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class TimeFormatServiceImpl implements TimeFormatService {

  private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy").withZone(ZoneId.systemDefault());
  private static final DateTimeFormatter dateFormatterSlashes = DateTimeFormatter.ofPattern("dd/MM/yyyy").withZone(ZoneId.systemDefault());
  private static final DateTimeFormatter timeFormatter = new DateTimeFormatterBuilder()
      .appendPattern("h:mm")
      .appendText(ChronoField.AMPM_OF_DAY, ImmutableMap.of(0L, "am", 1L, "pm"))
      .toFormatter()
      .withZone(ZoneId.systemDefault());

  @Override
  public String formatDateAndTime(long millis) {
    String date = dateFormatter.format(Instant.ofEpochMilli(millis));
    String time = timeFormatter.format(Instant.ofEpochMilli(millis));
    return date + " at " + time;
  }

  @Override
  public String formatDate(long millis) {
    return dateFormatter.format(Instant.ofEpochMilli(millis));
  }

  @Override
  public String formatDateWithSlashes(long millis) {
    return dateFormatterSlashes.format(Instant.ofEpochMilli(millis));
  }

}
