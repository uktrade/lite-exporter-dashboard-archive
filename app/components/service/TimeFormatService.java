package components.service;

public interface TimeFormatService {

  String formatDateAndTime(long millis);

  String formatOgelRegistrationDate(long millis);

  long parseOgelRegistrationDate(String ogelRegistrationDate);

  String formatDate(long millis);

  String formatDateWithSlashes(long millis);
}
