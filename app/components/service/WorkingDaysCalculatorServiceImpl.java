package components.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class WorkingDaysCalculatorServiceImpl implements WorkingDaysCalculatorService {

  @Override
  public long calculate(long start, long end) {
    return ChronoUnit.DAYS.between(Instant.ofEpochMilli(start), Instant.ofEpochMilli(end));
  }

}
