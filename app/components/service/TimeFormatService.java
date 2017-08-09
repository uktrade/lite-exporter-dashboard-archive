package components.service;

public interface TimeFormatService {

  String formatDateAndTime(long millis);

  String formatDate(long millis);

  String formatDateWithSlashes(long millis);
}
